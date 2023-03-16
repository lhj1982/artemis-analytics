package com.nike.artemis;

import com.nike.artemis.*;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;


import java.time.Duration;

public class Main {
    public static void main(String[] args) throws Exception {

        System.err.close();
        System.setErr(System.out);


        final StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());

        /*
        Properties consumerConfig = new Properties();
        consumerConfig.put(AWSConfigConstants.AWS_REGION, "cn-northwest-1");
        consumerConfig.put(AWSConfigConstants.AWS_ACCESS_KEY_ID, "aws_access_key_id");
        consumerConfig.put(AWSConfigConstants.AWS_SECRET_ACCESS_KEY, "aws_secret_access_key");
        consumerConfig.put(ConsumerConfigConstants.STREAM_INITIAL_POSITION, "LATEST");

        DataStream<String> sns = env.addSource(new FlinkKinesisConsumer<>(
                "isBot-sns", new SimpleStringSchema(), consumerConfig));

        sns.flatMap(new com.nike.artemis.SNSResolver());
         */


        // request event from sns
        DataStream<RequestEvent> requestEventDataStream = env.addSource(new SnsRequestGenerator())
                .uid("isBot-datasource")
                .assignTimestampsAndWatermarks(WatermarkStrategy.<RequestEvent>forBoundedOutOfOrderness(Duration.ofSeconds(30))
                        .withTimestampAssigner((SerializableTimestampAssigner<RequestEvent>) (element, recordTimestamp) -> element.getTimestamp())
                        .withIdleness(Duration.ofSeconds(10)));



        // rule from s3
        MapStateDescriptor<RateRule, Object> ruleStateDescriptor = new MapStateDescriptor<>("RulesBroadcastState", TypeInformation.of(new TypeHint<RateRule>() {}), BasicTypeInfo.of(Object.class));
        BroadcastStream<RuleChange> rulesSource = env.addSource(new RuleSource()).uid("Rules Source").broadcast(ruleStateDescriptor);



        requestEventDataStream.print("requestEventStream: ");
        requestEventDataStream
                .connect(rulesSource)
                        .process(new RuleBroadCastProcessorFunction())
                                .assignTimestampsAndWatermarks(WatermarkStrategy.<Tuple3<String, RateRule, Long>>forBoundedOutOfOrderness(Duration.ofSeconds(30))
                                        .withTimestampAssigner(new SerializableTimestampAssigner<Tuple3<String, RateRule, Long>>() {
                                            @Override
                                            public long extractTimestamp(Tuple3<String, RateRule, Long> element, long recordTimestamp) {
                                                return element.f2;
                                            }
                                        }).withIdleness(Duration.ofSeconds(30)))
                                        .keyBy(new KeySelector<Tuple3<String, RateRule, Long>, Tuple2<String,RateRule>>() {
                                            @Override
                                            public Tuple2<String, RateRule> getKey(Tuple3<String, RateRule, Long> value) throws Exception {
                                                // f0 can be county name or device id or upmid
                                                return new Tuple2<>(value.f0, value.f1);
                                            }
                                        })
                .window(new RateRuleWindowAssigner())
                .trigger(new RuleTrigger())
                .aggregate(new RuleCountAggregator(), new RuleProcessWindowFunction()).uid("Rule Window");
        
        


//        requestEventDataStream.keyBy(data -> data.getAddresses().get(0).getCounty())
//                .process(new KeyedProcessFunction<String, com.nike.artemis.RequestEvent, String>() {
//                    @Override
//                    public void processElement(com.nike.artemis.RequestEvent value, KeyedProcessFunction<String, com.nike.artemis.RequestEvent, String>.Context ctx, Collector<String> out) throws Exception {
//
//                    }
//                })
//                                .print("country:")
//                                        .uid("sum");

//        requestEventDataStream.print();






        env.execute();
    }
}
