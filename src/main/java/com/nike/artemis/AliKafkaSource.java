package com.nike.artemis;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.KafkaSourceBuilder;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Properties;

public class AliKafkaSource {


    public static KafkaSource<String> getKafkaSource(StreamExecutionEnvironment env, Properties appProperties) {

        KafkaSourceBuilder<String> builder = KafkaSource.builder();

        String brokers = appProperties.get(KafkaHelpers.ALI_KAFKA_BOOTSTRAP_SERVERS).toString();
        String inputTopic = appProperties.get(KafkaHelpers.KAFKA_SOURCE_TOPIC_KEY).toString();
        String consumerGroupId = appProperties.get(KafkaHelpers.KAFKA_CONSUMER_GROUP_ID_KEY).toString();

        configureConnectorPropsWithConfigProviders(builder, appProperties);

        KafkaSource<String> source = builder
                .setBootstrapServers(brokers)
                .setTopics(inputTopic)
                .setGroupId(consumerGroupId)
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        return source;
    }

    private static void configureConnectorPropsWithConfigProviders(KafkaSourceBuilder<String> builder, Properties appProperties) {
        // see https://github.com/aws-samples/msk-config-providers

        // define names of config providers:
        builder.setProperty("config.providers", "secretsmanager,s3import");

        // provide implementation classes for each provider:
        builder.setProperty("config.providers.secretsmanager.class", "com.amazonaws.kafka.config.providers.SecretsManagerConfigProvider");
        builder.setProperty("config.providers.s3import.class", "com.amazonaws.kafka.config.providers.S3ImportConfigProvider");

        // TODO: get the username and password from AWS SSM
        String truststoreS3Bucket = appProperties.get(KafkaHelpers.TRUSTSTORE_S3_BUCKET_KEY).toString();
        String truststoreS3Path = appProperties.get(KafkaHelpers.TRUSTSTORE_S3_PATH_KEY).toString();


        // properties
        builder.setProperty("ssl.truststore.location", "${s3import::" + truststoreS3Bucket + "/" + truststoreS3Path + "}");
        builder.setProperty("ssl.truststore.password", "KafkaOnsClient");
        builder.setProperty("security.protocol", "SASL_SSL");
        builder.setProperty("sasl.mechanism", "PLAIN");
        builder.setProperty("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"alikafka_post-cn-wwo3aprq4009\" password=\"eTdXc92kLfJSAAM3fmtXTZOT9CYxs7Zk\";");
        builder.setProperty("ssl.endpoint.identification.algorithm", "");

    }

}
