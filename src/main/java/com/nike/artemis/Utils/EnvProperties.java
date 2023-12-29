package com.nike.artemis.Utils;

import org.apache.flink.streaming.connectors.kinesis.config.AWSConfigConstants;
import org.apache.flink.streaming.connectors.kinesis.config.ConsumerConfigConstants;

import java.util.*;

public class EnvProperties {
    public static final String AWS_REGION = "cn-northwest-1";
    public static final String RUNTIME_PROPERTIES_S3_BUCKET = "rulesBucket";
    public static final String RUNTIME_PROPERTIES_CHECK_PHONE_NUMBER_FOR_PATH = "checkPath";

    public static Properties kinesisConsumerConfig() {
        Properties consumerConfig = new Properties();
        consumerConfig.put(AWSConfigConstants.AWS_REGION, AWS_REGION);
        consumerConfig.put(ConsumerConfigConstants.STREAM_INITIAL_POSITION, "LATEST");
        return consumerConfig;
    }

    public static Properties nemesisConfig(Map<String, Properties> applicationProperties) {
        Properties nemesisConfig = applicationProperties.get(RUNTIME_PROPERTIES_S3_BUCKET);
        nemesisConfig.setProperty("RulesRegionName", AWS_REGION);
        return nemesisConfig;
    }

    public static Properties kinesisProducerConfig() {
        Properties producerConfig = new Properties();
        producerConfig.setProperty(ConsumerConfigConstants.AWS_REGION, AWS_REGION);
        producerConfig.setProperty("AggregationEnabled", "false");
        return producerConfig;
    }

    public static List<String> wafRequestPaths(Map<String, Properties> applicationProperties) {
        List<String> wafRequestPaths = new ArrayList<>();
        Properties properties = applicationProperties.get(RUNTIME_PROPERTIES_CHECK_PHONE_NUMBER_FOR_PATH);
        for (Enumeration<Object> elements = properties.elements(); elements.hasMoreElements(); ) {
            String element = elements.nextElement().toString();
            wafRequestPaths.addAll(List.of(element.split(",")));
        }
        return wafRequestPaths;
    }

}
