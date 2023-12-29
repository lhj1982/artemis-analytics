package com.nike.artemis.processingPipeline;

import com.nike.artemis.LogMsgBuilder;
import com.nike.artemis.Utils.AliKafkaSource;
import com.nike.artemis.Utils.EnvProperties;
import com.nike.artemis.Utils.KafkaHelpers;
import com.nike.artemis.WindowAssigners.WafRateRuleWindowAssigner;
import com.nike.artemis.aggregators.WafRuleCountAggregate;
import com.nike.artemis.broadcastProcessors.WafRuleBroadCastProcessorFunction;
import com.nike.artemis.dataResolver.WafLogResolver;
import com.nike.artemis.model.block.Block;
import com.nike.artemis.model.rules.WafRateRule;
import com.nike.artemis.model.waf.WafRequestEvent;
import com.nike.artemis.processWindows.WafRuleProcessWindowFunction;
import com.nike.artemis.ruleChanges.WafRuleChange;
import com.nike.artemis.ruleProvider.S3RuleSourceProviderImpl;
import com.nike.artemis.ruleSources.WafRuleSource;
import com.nike.artemis.ruleTriggerer.WafRuleTrigger;
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

public class WafBlockProcessingPipeline extends BlockProcessingPipeline {
    public static Logger LOG = LoggerFactory.getLogger(WafBlockProcessingPipeline.class);

    // WAF Source from Kafka
    private DataStream<WafRequestEvent> dataSource(StreamExecutionEnvironment env, Map<String, Properties> applicationProperties) {
        Properties wafLogKafkaProperties = KafkaHelpers.getWafLogKafkaProperties(applicationProperties);
        if (wafLogKafkaProperties == null) {
            LOG.error(LogMsgBuilder.getInstance().msg("Incorrectly WAF log specified application properties. Exiting...").toString());
            return null;
        }
        LOG.info(LogMsgBuilder.getInstance().msg("properties of waf log kafka: " + wafLogKafkaProperties).toString());

        KafkaSource<String> wafKafkaSource = AliKafkaSource.getKafkaSource(wafLogKafkaProperties);
        return env.fromSource(wafKafkaSource, WatermarkStrategy.noWatermarks(),
                "WAF Log Kafka Source").flatMap(new WafLogResolver(EnvProperties.wafRequestPaths(applicationProperties)));
    }

    // WAF Rule from S3
    private BroadcastStream<WafRuleChange> ruleSource(StreamExecutionEnvironment env, Map<String, Properties> applicationProperties) {
        S3RuleSourceProviderImpl s3RuleSourceProvider = new S3RuleSourceProviderImpl(EnvProperties.nemesisConfig(applicationProperties));
        MapStateDescriptor<WafRateRule, Object> wafRulesStateDescriptor = new MapStateDescriptor<>("WafRulesBroadcastState",
                TypeInformation.of(new TypeHint<WafRateRule>() {
                }), BasicTypeInfo.of(Object.class));
        return env.addSource(new WafRuleSource(s3RuleSourceProvider, false))
                .name("WAF Rule Source S3").broadcast(wafRulesStateDescriptor);
    }

    @Override
    public DataStream<Block> process(StreamExecutionEnvironment env, Map<String, Properties> applicationProperties) {
        DataStream<WafRequestEvent> wafDataSource = this.dataSource(env, applicationProperties);
        BroadcastStream<WafRuleChange> wafRuleSource = this.ruleSource(env, applicationProperties);

        if (Objects.isNull(wafDataSource))
            throw new RuntimeException("Incorrectly WAF log specified application properties. Exiting...");

        return wafDataSource
                .connect(wafRuleSource)
                .process(new WafRuleBroadCastProcessorFunction())
                .assignTimestampsAndWatermarks(WatermarkStrategy.<Tuple3<String, WafRateRule, Long>>forBoundedOutOfOrderness(Duration.ofSeconds(30))
                        .withTimestampAssigner(new SerializableTimestampAssigner<Tuple3<String, WafRateRule, Long>>() {
                            @Override
                            public long extractTimestamp(Tuple3<String, WafRateRule, Long> element, long recordTimestamp) {
                                return element.f2;
                            }
                        }).withIdleness(Duration.ofSeconds(10)))
                .keyBy(new KeySelector<Tuple3<String, WafRateRule, Long>, Tuple2<String, WafRateRule>>() {
                    @Override
                    public Tuple2<String, WafRateRule> getKey(Tuple3<String, WafRateRule, Long> value) {
                        return new Tuple2<>(value.f0, value.f1);
                    }
                })
                .window(new WafRateRuleWindowAssigner())
                .trigger(new WafRuleTrigger())
                .aggregate(new WafRuleCountAggregate(), new WafRuleProcessWindowFunction())
                .name("WAF Log processor");
    }

    @Override
    public void execute(StreamExecutionEnvironment env, Map<String, Properties> appProperties, FlinkKinesisProducer<Block> sink) {
        DataStream<Block> block = this.process(env, appProperties);
        this.sink(sink, block, "WAF Block sink");
    }
}
