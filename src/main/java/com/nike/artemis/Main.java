package com.nike.artemis;

import com.nike.artemis.*;
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
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import software.amazon.kinesis.connectors.flink.FlinkKinesisConsumer;
import software.amazon.kinesis.connectors.flink.FlinkKinesisProducer;
import software.amazon.kinesis.connectors.flink.KinesisPartitioner;
import software.amazon.kinesis.connectors.flink.config.AWSConfigConstants;
import software.amazon.kinesis.connectors.flink.config.ConsumerConfigConstants;


import java.time.Duration;
import java.util.Properties;

public class Main {
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
        nemesisConfig.setProperty("RulesBucketName", "artemis-analytics");
        nemesisConfig.setProperty("RulesKeyName", "rules/rule");

        Properties producerConfig = new Properties();
        producerConfig.setProperty(ConsumerConfigConstants.AWS_REGION, region);
        producerConfig.setProperty("AggregationEnabled","false");


        //=============================== SNS REQUEST DATA STREAM ================

        DataStream<RequestEvent> requestEventDataStream = env.addSource(new FlinkKinesisConsumer<>(
                "artemis-input-stream", new SimpleStringSchema(), consumerConfig)).flatMap(new SNSResolver());


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
                .process(new RuleBroadCastProcessorFunction())
                .assignTimestampsAndWatermarks(WatermarkStrategy.<Tuple3<String, RateRule, Long>>forBoundedOutOfOrderness(Duration.ofSeconds(0))
                        .withTimestampAssigner(new SerializableTimestampAssigner<Tuple3<String, RateRule, Long>>() {
                            @Override
                            public long extractTimestamp(Tuple3<String, RateRule, Long> element, long recordTimestamp) {
                                return element.f2;
                            }
                        }).withIdleness(Duration.ofSeconds(30)))
                .keyBy(new KeySelector<Tuple3<String, RateRule, Long>, Tuple2<String, RateRule>>() {
                    @Override
                    public Tuple2<String, RateRule> getKey(Tuple3<String, RateRule, Long> value) throws Exception {
                        return new Tuple2<>(value.f0, value.f1);
                    }
                })
                .window(TumblingEventTimeWindows.of(Time.minutes(10)))
                .trigger(new RuleTrigger())
                .aggregate(new RuleCountAggregator(), new RuleProcessWindowFunction()).uid("Rule Window");

        FlinkKinesisProducer<BlockEvent> sink = new FlinkKinesisProducer<>(BlockEvent.sinkSerializer(), producerConfig);
        sink.setDefaultStream("artemis-blocker-stream");
        sink.setCustomPartitioner(new KinesisPartitioner<BlockEvent>() {
            @Override
            public String getPartitionId(BlockEvent element) {
                return element.getRuleName();
            }
        });

        outputStream.addSink(sink).uid("Output");

        env.execute();
    }
}
