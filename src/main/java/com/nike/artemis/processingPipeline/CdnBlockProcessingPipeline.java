package com.nike.artemis.processingPipeline;

import com.nike.artemis.LogMsgBuilder;
import com.nike.artemis.Utils.AliKafkaSource;
import com.nike.artemis.Utils.EnvProperties;
import com.nike.artemis.Utils.KafkaHelpers;
import com.nike.artemis.WindowAssigners.CdnRateRuleWindowAssigner;
import com.nike.artemis.aggregators.CdnRuleCountAggregate;
import com.nike.artemis.broadcastProcessors.CdnRuleBroadCastProcessorFunction;
import com.nike.artemis.dataResolver.CdnLogResolver;
import com.nike.artemis.model.block.Block;
import com.nike.artemis.model.cdn.CdnRequestEvent;
import com.nike.artemis.model.rules.CdnRateRule;
import com.nike.artemis.processWindows.CdnRuleProcessWindowFunction;
import com.nike.artemis.ruleChanges.CdnRuleChange;
import com.nike.artemis.ruleProvider.S3RuleSourceProviderImpl;
import com.nike.artemis.ruleSources.CdnRuleSource;
import com.nike.artemis.ruleTriggerer.CdnRuleTrigger;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kinesis.FlinkKinesisProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

public class CdnBlockProcessingPipeline extends BlockProcessingPipeline {
    public static Logger LOG = LoggerFactory.getLogger(CdnBlockProcessingPipeline.class);

    // CDN Source from Kafka
    private DataStream<CdnRequestEvent> dataSource(StreamExecutionEnvironment env, Map<String, Properties> applicationProperties) {
        Properties cdnLogKafkaProperties = KafkaHelpers.getCdnLogKafkaProperties(applicationProperties);
        if (Objects.isNull(cdnLogKafkaProperties)) {
            LOG.error(LogMsgBuilder.getInstance().msg("Incorrectly CDN log specified application properties. Exiting...").toString());
            return null;
        }
        LOG.info(LogMsgBuilder.getInstance().msg("properties of cdn log kafka: " + cdnLogKafkaProperties).toString());

        KafkaSource<String> cdnKafkaSource = AliKafkaSource.getKafkaSource(cdnLogKafkaProperties);
        return env.fromSource(cdnKafkaSource, WatermarkStrategy.noWatermarks(),
                "CDN Log Kafka Source").flatMap(new CdnLogResolver());
    }

    // CDN Rule from S3
    private BroadcastStream<CdnRuleChange> ruleSource(StreamExecutionEnvironment env, Map<String, Properties> applicationProperties) {
        S3RuleSourceProviderImpl s3RuleSourceProvider = new S3RuleSourceProviderImpl(EnvProperties.nemesisConfig(applicationProperties));
        MapStateDescriptor<CdnRateRule, Object> cdnRuleStateDescriptor = new MapStateDescriptor<>("CdnRulesBroadcastState",
                TypeInformation.of(new TypeHint<CdnRateRule>() {
                }),
                BasicTypeInfo.of(Object.class));
        return env.addSource(new CdnRuleSource(s3RuleSourceProvider, false)).name("CDN Rule Source S3")
                .broadcast(cdnRuleStateDescriptor);
    }

    @Override
    public DataStream<Block> process(StreamExecutionEnvironment env, Map<String, Properties> applicationProperties) {
        DataStream<CdnRequestEvent> cdnDataSource = this.dataSource(env, applicationProperties);
        BroadcastStream<CdnRuleChange> cdnRuleSource = this.ruleSource(env, applicationProperties);

        if (Objects.isNull(cdnDataSource))
            throw new RuntimeException("Incorrectly WAF log specified application properties. Exiting...");

        return cdnDataSource
                .connect(cdnRuleSource)
                .process(new CdnRuleBroadCastProcessorFunction()).name("BroadCast CDN Rules to Cdn Request Event")
                .assignTimestampsAndWatermarks(WatermarkStrategy.<Tuple3<String, CdnRateRule, Long>>forBoundedOutOfOrderness(Duration.ofSeconds(30))
                        .withTimestampAssigner(new SerializableTimestampAssigner<Tuple3<String, CdnRateRule, Long>>() {
                            @Override
                            public long extractTimestamp(Tuple3<String, CdnRateRule, Long> element, long recordTimestamp) {
                                return element.f2;
                            }
                        }).withIdleness(Duration.ofSeconds(10)))
                .keyBy(new KeySelector<Tuple3<String, CdnRateRule, Long>, Tuple2<String, CdnRateRule>>() {
                    @Override
                    public Tuple2<String, CdnRateRule> getKey(Tuple3<String, CdnRateRule, Long> value) {
                        return new Tuple2<>(value.f0, value.f1);
                    }
                })
                .window(new CdnRateRuleWindowAssigner())
                .trigger(new CdnRuleTrigger())
                .aggregate(new CdnRuleCountAggregate(), new CdnRuleProcessWindowFunction())
                .name("CDN Log processor");
    }

    @Override
    public void execute(StreamExecutionEnvironment env, Map<String, Properties> appProperties, FlinkKinesisProducer<Block> sink) {
        DataStream<Block> block = this.process(env, appProperties);
        this.sink(sink, block, "CDN Block sink");
    }
}
