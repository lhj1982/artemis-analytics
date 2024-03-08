package com.nike.artemis.processingPipeline;

import com.nike.artemis.Utils.EnvProperties;
import com.nike.artemis.cloudWatchMetricsSink.CloudWatchMetricsSink;
import com.nike.artemis.dataResolver.IsBotMessageResolver;
import com.nike.artemis.model.Latency;
import com.nike.artemis.model.block.Block;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kinesis.sink.KinesisStreamsSink;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kinesis.FlinkKinesisConsumer;

import java.util.Map;
import java.util.Properties;

public class IsBotBlockProcessingPipeline extends BlockProcessingPipeline {

    private DataStream<Block> dataSource(StreamExecutionEnvironment env, Map<String, Properties> applicationProperties) {
        return env.addSource(new FlinkKinesisConsumer<>("bots-isbot-result-stream", new SimpleStringSchema(), EnvProperties.kinesisCrossAccountConfig(applicationProperties)))
                .flatMap(new IsBotMessageResolver())
                .name("IsBot Result Input").disableChaining();
    }

    private DataStream<Block> process(StreamExecutionEnvironment env, Map<String, Properties> appProperties) {
        return this.dataSource(env, appProperties);
    }

    @Override
    public void execute(StreamExecutionEnvironment env, Map<String, Properties> appProperties,
                        KinesisStreamsSink<Block> kinesisStreamsSink,
                        CloudWatchMetricsSink<Latency> cloudWatchMetricsSink) {
        // block process
        DataStream<Block> block = this.process(env, appProperties);
        this.sink(kinesisStreamsSink, block, "IsBot Block Sink");

    }
}
