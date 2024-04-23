package com.nike.artemis;

import com.amazonaws.services.kinesisanalytics.runtime.KinesisAnalyticsRuntime;
import com.nike.artemis.Utils.EnvProperties;
import com.nike.artemis.cloudWatchMetricsSink.CloudWatchMetricsSink;
import com.nike.artemis.model.Latency;
import com.nike.artemis.model.block.Block;
import com.nike.artemis.processingPipeline.*;
import org.apache.flink.connector.kinesis.sink.KinesisStreamsSink;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.cloudwatch.model.Dimension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Main {
    public static Logger LOG = LoggerFactory.getLogger(Main.class);

    private static final String RUNTIME_PROPERTIES_SINK = "sink";
    private static final String RUNTIME_PROPERTIES_SINK_METHODS = "methods";
    public static volatile Integer KPU_NUM;

    public static void main(String[] args) throws Exception {
        System.err.close();
        System.setErr(System.out);

        Map<String, BlockProcessingPipeline> map = new HashMap<>() {{
            put("cdn", new CdnBlockProcessingPipeline());
            put("waf", new WafBlockProcessingPipeline());
            put("launch", new LaunchBlockProcessingPipeline());
            put("isbot", new IsBotBlockProcessingPipeline());
        }};

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        KPU_NUM = env.getParallelism();

        //=============================== PROPERTIES =============================
        Map<String, Properties> applicationProperties = KinesisAnalyticsRuntime.getApplicationProperties();

        //=============================== Block Sink =============================
        KinesisStreamsSink<Block> kinesisStreamsSink = KinesisStreamsSink.<Block>builder()
                .setKinesisClientProperties(EnvProperties.kinesisProducerConfig())
                .setSerializationSchema(Block.sinkSerializer())
                .setStreamName("artemis-blocker-stream")
                .setPartitionKeyGenerator(Block::getUser)
                .build();

        //=============================== Cloud Watch Metric Sink =============================
        CloudWatchMetricsSink<Latency> cloudWatchMetricsSink = CloudWatchMetricsSink.<Latency>builder()
                .clientProperties(EnvProperties.cloudWatchMetricConfig())
                .namespace("cloudwatch-metrics-artemis")
                .metricName("artemis_latency")
                .valueExtractor(Latency::getLatency)
                .timestampExtractor(Latency::getTimestamp)
                .dimensionsExtractor(l -> List.of(
                        Dimension.builder()
                                .name("type")
                                .value(l.getType())
                                .build()))
                .build();

        // add all sinks
        Properties sinkProperties = applicationProperties.get(RUNTIME_PROPERTIES_SINK);
        String methods = sinkProperties.getProperty(RUNTIME_PROPERTIES_SINK_METHODS);
        List<String> sinkMethods = List.of(methods.split(","));
        sinkMethods.forEach(method -> {
            map.get(method).execute(env, applicationProperties, kinesisStreamsSink, cloudWatchMetricsSink);
        });

        env.execute();
    }
}
