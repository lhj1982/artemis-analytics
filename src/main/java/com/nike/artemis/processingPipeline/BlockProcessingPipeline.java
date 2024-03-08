package com.nike.artemis.processingPipeline;

import com.nike.artemis.cloudWatchMetricsSink.CloudWatchMetricsSink;
import com.nike.artemis.model.Latency;
import com.nike.artemis.model.block.Block;
import org.apache.flink.connector.kinesis.sink.KinesisStreamsSink;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Map;
import java.util.Properties;

public abstract class BlockProcessingPipeline {

//    public abstract DataStream<Block> process(StreamExecutionEnvironment env, Map<String, Properties> appProperties);

    public void sink(KinesisStreamsSink<Block> sink, DataStream<Block> block, String name) {
        block.sinkTo(sink).name(name);
    }

    public void latency(CloudWatchMetricsSink<Latency> sink, DataStream<Latency> latency, String name) {
        latency.sinkTo(sink).name(name);
    }

    public abstract void execute(StreamExecutionEnvironment env, Map<String, Properties> appProperties,
                                 KinesisStreamsSink<Block> kinesisStreamsSink,
                                 CloudWatchMetricsSink<Latency> cloudWatchMetricsSink);
}
