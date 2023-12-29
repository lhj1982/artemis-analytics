package com.nike.artemis.processingPipeline;

import com.nike.artemis.model.block.Block;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kinesis.FlinkKinesisProducer;

import java.util.Map;
import java.util.Properties;

public abstract class BlockProcessingPipeline {

    public abstract DataStream<Block> process(StreamExecutionEnvironment env, Map<String, Properties> appProperties);

    public void sink(FlinkKinesisProducer<Block> sink, DataStream<Block> block, String name) {
        block.addSink(sink).name(name);
    }

    public abstract void execute(StreamExecutionEnvironment env, Map<String, Properties> appProperties, FlinkKinesisProducer<Block> sink);
}
