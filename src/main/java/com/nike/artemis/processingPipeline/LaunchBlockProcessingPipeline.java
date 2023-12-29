package com.nike.artemis.processingPipeline;

import com.nike.artemis.Utils.EnvProperties;
import com.nike.artemis.WindowAssigners.LaunchRateRuleWindowAssigner;
import com.nike.artemis.aggregators.LaunchRuleCountAggregate;
import com.nike.artemis.broadcastProcessors.LaunchRuleBroadCastProcessorFunction;
import com.nike.artemis.dataResolver.SNSResolver;
import com.nike.artemis.model.block.Block;
import com.nike.artemis.model.launch.LaunchRequestEvent;
import com.nike.artemis.model.rules.LaunchRateRule;
import com.nike.artemis.processWindows.LaunchRuleProcessWindowFunction;
import com.nike.artemis.ruleChanges.LaunchRuleChange;
import com.nike.artemis.ruleProvider.S3RuleSourceProviderImpl;
import com.nike.artemis.ruleSources.LaunchRuleSource;
import com.nike.artemis.ruleTriggerer.LaunchRuleTrigger;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kinesis.FlinkKinesisConsumer;
import org.apache.flink.streaming.connectors.kinesis.FlinkKinesisProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Map;
import java.util.Properties;

public class LaunchBlockProcessingPipeline extends BlockProcessingPipeline {
    public static Logger LOG = LoggerFactory.getLogger(CdnBlockProcessingPipeline.class);

    // Launch Source from DATA STREAM(SNS)
    private DataStream<LaunchRequestEvent> dataSource(StreamExecutionEnvironment env) {
        return env.addSource(new FlinkKinesisConsumer<>("artemis-input-stream", new SimpleStringSchema(),
                EnvProperties.kinesisConsumerConfig())).flatMap(new SNSResolver()).name("Artemis Input");
    }

    // Launch Rule from S3
    private BroadcastStream<LaunchRuleChange> ruleSource(StreamExecutionEnvironment env, Map<String, Properties> applicationProperties) {
        S3RuleSourceProviderImpl s3RuleSourceProvider = new S3RuleSourceProviderImpl(EnvProperties.nemesisConfig(applicationProperties));
        MapStateDescriptor<LaunchRateRule, Object> ruleStateDescriptor = new MapStateDescriptor<>("LaunchRulesBroadcastState",
                TypeInformation.of(new TypeHint<LaunchRateRule>() {
                }), BasicTypeInfo.of(Object.class));
        return env.addSource(new LaunchRuleSource(s3RuleSourceProvider, false))
                .name("LAUNCH Rule Source S3").broadcast(ruleStateDescriptor);
    }

    @Override
    public DataStream<Block> process(StreamExecutionEnvironment env, Map<String, Properties> applicationProperties) {
        DataStream<LaunchRequestEvent> dataSource = this.dataSource(env);
        BroadcastStream<LaunchRuleChange> ruleSource = this.ruleSource(env, applicationProperties);
        return dataSource
                .connect(ruleSource)
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
    }

    @Override
    public void execute(StreamExecutionEnvironment env, Map<String, Properties> appProperties, FlinkKinesisProducer<Block> sink) {
        DataStream<Block> block = this.process(env, appProperties);
        this.sink(sink, block, "LAUNCH Block sink");
    }
}
