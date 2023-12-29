package com.nike.artemis;

import com.amazonaws.services.kinesisanalytics.runtime.KinesisAnalyticsRuntime;
import com.nike.artemis.Utils.EnvProperties;
import com.nike.artemis.model.block.Block;
import com.nike.artemis.processingPipeline.BlockProcessingPipeline;
import com.nike.artemis.processingPipeline.CdnBlockProcessingPipeline;
import com.nike.artemis.processingPipeline.LaunchBlockProcessingPipeline;
import com.nike.artemis.processingPipeline.WafBlockProcessingPipeline;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kinesis.FlinkKinesisProducer;
import org.apache.flink.streaming.connectors.kinesis.KinesisPartitioner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Main {
    public static Logger LOG = LoggerFactory.getLogger(Main.class);

    private static final String RUNTIME_PROPERTIES_SINK = "sink";
    private static final String RUNTIME_PROPERTIES_SINK_METHODS = "methods";

    public static void main(String[] args) throws Exception {
        System.err.close();
        System.setErr(System.out);

        Map<String, BlockProcessingPipeline> map = new HashMap<>() {{
            put("cdn", new CdnBlockProcessingPipeline());
            put("waf", new WafBlockProcessingPipeline());
            put("launch", new LaunchBlockProcessingPipeline());
        }};

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        //=============================== PROPERTIES =============================
        Map<String, Properties> applicationProperties = KinesisAnalyticsRuntime.getApplicationProperties();

        //=============================== Block Sink =============================
        FlinkKinesisProducer<Block> sink = new FlinkKinesisProducer<>(Block.sinkSerializer(), EnvProperties.kinesisConsumerConfig());
        sink.setDefaultStream("artemis-blocker-stream");
        sink.setCustomPartitioner(new KinesisPartitioner<Block>() {
            @Override
            public String getPartitionId(Block element) {
                return element.getUser();
            }
        });

        // add all sinks
        Properties sinkProperties = applicationProperties.get(RUNTIME_PROPERTIES_SINK);
        String methods = sinkProperties.getProperty(RUNTIME_PROPERTIES_SINK_METHODS);
        List<String> sinkMethods = List.of(methods.split(","));
        sinkMethods.forEach(method -> {
            map.get(method).execute(env, applicationProperties, sink);
        });

        env.execute();
    }
}
