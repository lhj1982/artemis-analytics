package com.nike.artemis;

import com.amazonaws.services.kinesisanalytics.runtime.KinesisAnalyticsRuntime;
import com.nike.artemis.Utils.AliKafkaSource;
import com.nike.artemis.Utils.KafkaHelpers;
import com.nike.artemis.WindowAssigners.LaunchRateRuleWindowAssigner;
import com.nike.artemis.aggregators.LaunchRuleCountAggregate;
import com.nike.artemis.broadcastProcessors.LaunchRuleBroadCastProcessorFunction;
import com.nike.artemis.model.launch.LaunchRequestEvent;
import com.nike.artemis.model.rules.LaunchRateRule;
import com.nike.artemis.processWindows.LaunchRuleProcessWindowFunction;
import com.nike.artemis.ruleChanges.LaunchRuleChange;
import com.nike.artemis.ruleProvider.S3RuleSourceProviderImpl;
import com.nike.artemis.WindowAssigners.CdnRateRuleWindowAssigner;
import com.nike.artemis.WindowAssigners.WafRateRuleWindowAssigner;
import com.nike.artemis.aggregators.CdnRuleCountAggregate;
import com.nike.artemis.aggregators.WafRuleCountAggregate;
import com.nike.artemis.broadcastProcessors.CdnRuleBroadCastProcessorFunction;
import com.nike.artemis.broadcastProcessors.WafRuleBroadCastProcessorFunction;
import com.nike.artemis.dataResolver.CdnLogResolver;
import com.nike.artemis.dataResolver.SNSResolver;
import com.nike.artemis.dataResolver.WafLogResolver;
import com.nike.artemis.model.block.Block;
import com.nike.artemis.model.cdn.CdnRequestEvent;
import com.nike.artemis.model.rules.CdnRateRule;
import com.nike.artemis.model.rules.WafRateRule;
import com.nike.artemis.model.waf.WafRequestEvent;
import com.nike.artemis.processWindows.CdnRuleProcessWindowFunction;
import com.nike.artemis.processWindows.WafRuleProcessWindowFunction;
import com.nike.artemis.ruleChanges.CdnRuleChange;
import com.nike.artemis.ruleChanges.WafRuleChange;
import com.nike.artemis.ruleSources.CdnRuleSource;
import com.nike.artemis.ruleSources.LaunchRuleSource;
import com.nike.artemis.ruleSources.WafRuleSource;
import com.nike.artemis.ruleTriggerer.CdnRuleTrigger;
import com.nike.artemis.ruleTriggerer.LaunchRuleTrigger;
import com.nike.artemis.ruleTriggerer.WafRuleTrigger;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kinesis.FlinkKinesisConsumer;
import org.apache.flink.streaming.connectors.kinesis.FlinkKinesisProducer;
import org.apache.flink.streaming.connectors.kinesis.KinesisPartitioner;
import org.apache.flink.streaming.connectors.kinesis.config.AWSConfigConstants;
import org.apache.flink.streaming.connectors.kinesis.config.ConsumerConfigConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.time.Duration;
import java.util.Map;
import java.util.Properties;

public class Main {
    public static Logger LOG = LoggerFactory.getLogger(Main.class);

    public static final String RUNTIME_PROPERTIES_S3_BUCKET = "rulesBucket";

    public static void main(String[] args) throws Exception {

        System.err.close();
        System.setErr(System.out);


        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        //=============================== PROPERTIES =============================

        Map<String, Properties> applicationProperties = KinesisAnalyticsRuntime.getApplicationProperties();

        String region = "cn-northwest-1";

        Properties consumerConfig = new Properties();
        consumerConfig.put(AWSConfigConstants.AWS_REGION, region);
        consumerConfig.put(ConsumerConfigConstants.STREAM_INITIAL_POSITION, "LATEST");

        Properties nemesisConfig = applicationProperties.get(RUNTIME_PROPERTIES_S3_BUCKET);
        nemesisConfig.setProperty("RulesRegionName", region);

        Properties producerConfig = new Properties();
        producerConfig.setProperty(ConsumerConfigConstants.AWS_REGION, region);
        producerConfig.setProperty("AggregationEnabled", "false");
        S3RuleSourceProviderImpl s3RuleSourceProvider = new S3RuleSourceProviderImpl(nemesisConfig);


        //=============================== SNS REQUEST DATA STREAM ================

        DataStream<LaunchRequestEvent> requestEventDataStream = env.addSource(new FlinkKinesisConsumer<>(
                        "artemis-input-stream", new SimpleStringSchema(), consumerConfig)).flatMap(new SNSResolver())
                .name("Artemis Input");


        //=============================== ALI KAFKA SOURCE =======================

        Properties cdnLogKafkaProperties = KafkaHelpers.getCdnLogKafkaProperties(applicationProperties);
        if (cdnLogKafkaProperties == null) {
            LOG.error(LogMsgBuilder.getInstance()
                    .msg("Incorrectly CDN log specified application properties. Exiting...")
                    .build().toString());
            return;
        }
        LOG.info(LogMsgBuilder.getInstance()
                .msg(String.format("properties of cdn log kafka: %s", cdnLogKafkaProperties))
                .build().toString());

        KafkaSource<String> cdnKafkaSource = AliKafkaSource.getKafkaSource(env, cdnLogKafkaProperties);
        DataStream<CdnRequestEvent> cdn_log_kafka_source = env.fromSource(cdnKafkaSource, WatermarkStrategy.noWatermarks(), "CDN Log Kafka Source")
                .flatMap(new CdnLogResolver());

        MapStateDescriptor<CdnRateRule, Object> cdnRuleStateDescriptor = new MapStateDescriptor<>("CdnRulesBroadcastState",
                TypeInformation.of(new TypeHint<CdnRateRule>() {}),
                BasicTypeInfo.of(Object.class));
        BroadcastStream<CdnRuleChange> cdnRuleDS = env.addSource(new CdnRuleSource(s3RuleSourceProvider, false))
                .name("CDN Rule Source S3").broadcast(cdnRuleStateDescriptor);

        DataStream<Block> cdnBlockDs = cdn_log_kafka_source
                .connect(cdnRuleDS)
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
                    public Tuple2<String, CdnRateRule> getKey(Tuple3<String, CdnRateRule, Long> value) throws Exception {
                        return new Tuple2<>(value.f0, value.f1);
                    }
                })
                .window(new CdnRateRuleWindowAssigner())
                .trigger(new CdnRuleTrigger())
                .aggregate(new CdnRuleCountAggregate(), new CdnRuleProcessWindowFunction())
                .name("CDN Log processor");


        Properties wafLogKafkaProperties = KafkaHelpers.getWafLogKafkaProperties(applicationProperties);
        if (wafLogKafkaProperties == null) {
            LOG.error(LogMsgBuilder.getInstance()
                    .msg("Incorrectly WAF log specified application properties. Exiting...")
                    .build().toString());
            return;
        }
        LOG.info(LogMsgBuilder.getInstance()
                .msg(String.format("properties of waf log kafka: %s", wafLogKafkaProperties))
                .build().toString());
        KafkaSource<String> wafKafkaSource = AliKafkaSource.getKafkaSource(env, wafLogKafkaProperties);
        DataStream<WafRequestEvent> waf_log_kafka_source = env.fromSource(wafKafkaSource, WatermarkStrategy.noWatermarks(), "WAF Log Kafka Source")
                .flatMap(new WafLogResolver());

        MapStateDescriptor<WafRateRule, Object> wafRulesStateDescriptor = new MapStateDescriptor<>("WafRulesBroadcastState",
                TypeInformation.of(new TypeHint<WafRateRule>() {}), BasicTypeInfo.of(Object.class));
        BroadcastStream<WafRuleChange> wafRuleDs = env.addSource(new WafRuleSource(s3RuleSourceProvider, false))
                .name("WAF Rule Source S3").broadcast(wafRulesStateDescriptor);

        DataStream<Block> wafBlockDs = waf_log_kafka_source
                .connect(wafRuleDs)
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
                    public Tuple2<String, WafRateRule> getKey(Tuple3<String, WafRateRule, Long> value) throws Exception {
                        return new Tuple2<>(value.f0, value.f1);
                    }
                })
                .window(new WafRateRuleWindowAssigner())
                .trigger(new WafRuleTrigger())
                .aggregate(new WafRuleCountAggregate(), new WafRuleProcessWindowFunction())
                .name("WAF Log processor");

        //=============================== SNS EVENT SIMULATOR =====================
//        DataStream<RequestEvent> requestEventDataStream = env.addSource(new SnsRequestGenerator())
//                .uid("isBot-datasource-simulation");


        //=============================== Rule from S3 ===========================
        MapStateDescriptor<LaunchRateRule, Object> ruleStateDescriptor = new MapStateDescriptor<>("LaunchRulesBroadcastState",
                TypeInformation.of(new TypeHint<LaunchRateRule>() {}), BasicTypeInfo.of(Object.class));
        BroadcastStream<LaunchRuleChange> rulesSource = env.addSource(new LaunchRuleSource(s3RuleSourceProvider, false))
                .name("LAUNCH Rule Source S3").broadcast(ruleStateDescriptor);

//        BroadcastStream<RuleChange> rulesSource = env.addSource(new RuleSource()).uid("Rules Source").broadcast(ruleStateDescriptor);


//        requestEventDataStream.print("requestEventStream: ");
        DataStream<Block> launchBlockDs = requestEventDataStream
                .connect(rulesSource)
                .process(new LaunchRuleBroadCastProcessorFunction()).name("Blend BroadCast Rule with Event")
                .assignTimestampsAndWatermarks(WatermarkStrategy.<Tuple4<String, String, LaunchRateRule, Long>>forBoundedOutOfOrderness(Duration.ofSeconds(10))
                        .withTimestampAssigner(new SerializableTimestampAssigner<Tuple4<String, String, LaunchRateRule, Long>>() {
                            @Override
                            public long extractTimestamp(Tuple4<String, String, LaunchRateRule, Long> element, long recordTimestamp) {
                                return element.f3;
                            }
                        }).withIdleness(Duration.ofSeconds(30)))
                .keyBy(new KeySelector<Tuple4<String, String, LaunchRateRule, Long>, Tuple3<String, String, LaunchRateRule>>() {
                    @Override
                    public Tuple3<String, String, LaunchRateRule> getKey(Tuple4<String, String, LaunchRateRule, Long> value) throws Exception {
                        return new Tuple3<>(value.f0, value.f1, value.f2); //entity, launchId, LaunchRateRule
                    }
                })
                .window(new LaunchRateRuleWindowAssigner())
                .trigger(new LaunchRuleTrigger())
                .aggregate(new LaunchRuleCountAggregate(), new LaunchRuleProcessWindowFunction())
                .name("Rule Window");

        FlinkKinesisProducer<Block> sink = new FlinkKinesisProducer<>(Block.sinkSerializer(), producerConfig);
        sink.setDefaultStream("artemis-blocker-stream");
        sink.setCustomPartitioner(new KinesisPartitioner<Block>() {
            @Override
            public String getPartitionId(Block element) {
                return element.getUser();
            }
        });

        cdnBlockDs.addSink(sink).name("CDN Block sink");
        wafBlockDs.addSink(sink).name("WAF Block sink");
        launchBlockDs.addSink(sink).name("LAUNCH Block sink");

        env.execute();
    }
}
