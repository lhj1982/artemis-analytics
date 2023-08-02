package com.nike.artemis;

import com.nike.artemis.model.cdn.CdnRequestEvent;
import com.nike.artemis.model.waf.WafRequestEvent;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kinesis.FlinkKinesisConsumer;
import org.apache.flink.streaming.connectors.kinesis.FlinkKinesisProducer;
import org.apache.flink.streaming.connectors.kinesis.KinesisPartitioner;
import org.apache.flink.streaming.connectors.kinesis.config.AWSConfigConstants;
import org.apache.flink.streaming.connectors.kinesis.config.ConsumerConfigConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.time.Duration;
import java.util.Properties;

public class Main {
    public static Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {

        System.err.close();
        System.setErr(System.out);


        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        //=============================== PROPERTIES =============================

        String region = "cn-northwest-1";

        Properties consumerConfig = new Properties();
        consumerConfig.put(AWSConfigConstants.AWS_REGION, region);
        consumerConfig.put(ConsumerConfigConstants.STREAM_INITIAL_POSITION, "LATEST");

        Properties nemesisConfig = new Properties();
        nemesisConfig.setProperty("RulesRegionName", region);
        nemesisConfig.setProperty("RulesBucketName", "litx-test-artemis-analytics");
        nemesisConfig.setProperty("RulesKeyName", "rules/rule");

        Properties producerConfig = new Properties();
        producerConfig.setProperty(ConsumerConfigConstants.AWS_REGION, region);
        producerConfig.setProperty("AggregationEnabled","false");


        //=============================== SNS REQUEST DATA STREAM ================

        DataStream<RequestEvent> requestEventDataStream = env.addSource(new FlinkKinesisConsumer<>(
                "artemis-input-stream", new SimpleStringSchema(), consumerConfig)).flatMap(new SNSResolver())
                .name("Artemis Input");


        //=============================== ALI KAFKA SOURCE =======================

        Properties cdnLogKafkaProperties = KafkaHelpers.getCdnLogKafkaProperties();
        if(cdnLogKafkaProperties == null) {
            LOG.error("Incorrectly CDN log specified application properties. Exiting...");
            return;
        }

        LOG.info("properties of cdn log kafka: {}",cdnLogKafkaProperties);
        KafkaSource<String> cdnKafkaSource = AliKafkaSource.getKafkaSource(env, cdnLogKafkaProperties);
        DataStream<CdnRequestEvent> cdn_log_kafka_source = env.fromSource(cdnKafkaSource, WatermarkStrategy.noWatermarks(), "CDN Log Kafka Source").flatMap(new CdnLogResolver())
                .map(data -> {
                    LOG.info("logs from Ali cloud CDN kafka: {}", data);
                    return data;
                });


        Properties wafLogKafkaProperties = KafkaHelpers.getWafLogKafkaProperties();
        if(wafLogKafkaProperties == null) {
            LOG.error("Incorrectly WAF log specified application properties. Exiting...");
            return;
        }
        LOG.info("properties of waf log kafka: {}", wafLogKafkaProperties);
        KafkaSource<String> wafKafkaSource = AliKafkaSource.getKafkaSource(env, wafLogKafkaProperties);
        DataStream<WafRequestEvent> waf_log_kafka_source = env.fromSource(wafKafkaSource, WatermarkStrategy.noWatermarks(), "WAF Log Kafka Source").flatMap(new WafLogResolver())
                .map(data -> {
                    LOG.info("logs from ali cloud WAF kafka: {}", data);
                    return data;
                });


        //=============================== SNS EVENT SIMULATOR =====================
//        DataStream<RequestEvent> requestEventDataStream = env.addSource(new SnsRequestGenerator())
//                .uid("isBot-datasource-simulation");



        //=============================== Rule from S3 ===========================
        MapStateDescriptor<RateRule, Object> ruleStateDescriptor = new MapStateDescriptor<>("RulesBroadcastState", TypeInformation.of(new TypeHint<RateRule>() {}), BasicTypeInfo.of(Object.class));
        BroadcastStream<RuleChange> rulesSource = env.addSource(new RuleSource(new S3RuleSourceProviderImpl(nemesisConfig))).broadcast(ruleStateDescriptor);

//        BroadcastStream<RuleChange> rulesSource = env.addSource(new RuleSource()).uid("Rules Source").broadcast(ruleStateDescriptor);


//        requestEventDataStream.print("requestEventStream: ");
         DataStream<BlockEvent> outputStream = requestEventDataStream
                .connect(rulesSource)
                .process(new RuleBroadCastProcessorFunction()).name("Blend BroadCast Rule with Event")
                .assignTimestampsAndWatermarks(WatermarkStrategy.<Tuple4<String, String, RateRule, Long>>forBoundedOutOfOrderness(Duration.ofSeconds(10))
                        .withTimestampAssigner(new SerializableTimestampAssigner<Tuple4<String, String, RateRule, Long>>() {
                            @Override
                            public long extractTimestamp(Tuple4<String, String, RateRule, Long> element, long recordTimestamp) {
                                return element.f3;
                            }
                        }).withIdleness(Duration.ofSeconds(30)))
                .keyBy(new KeySelector<Tuple4<String, String, RateRule, Long>, Tuple3<String, String, RateRule>>() {
                    @Override
                    public Tuple3<String, String, RateRule> getKey(Tuple4<String, String, RateRule , Long> value) throws Exception {
                        return new Tuple3<>(value.f0, value.f1, value.f2); //entity, launchId, RateRule
                    }
                })
                .window(TumblingEventTimeWindows.of(Time.minutes(10)))
                .trigger(new RuleTrigger())
                .aggregate(new RuleCountAggregator(), new RuleProcessWindowFunction()).name("Rule Window");

        FlinkKinesisProducer<BlockEvent> sink = new FlinkKinesisProducer<>(BlockEvent.sinkSerializer(), producerConfig);
        sink.setDefaultStream("artemis-blocker-stream");
        sink.setCustomPartitioner(new KinesisPartitioner<BlockEvent>() {
            @Override
            public String getPartitionId(BlockEvent element) {
                return element.getEntity();
            }
        });

        outputStream.addSink(sink).name("Artemis Output Block");

        env.execute();
    }
}
