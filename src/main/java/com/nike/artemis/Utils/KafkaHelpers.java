package com.nike.artemis.Utils;

import com.nike.artemis.LogMsgBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class KafkaHelpers {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaHelpers.class);

    public static final String RUNTIME_PROPERTIES_KAFKA_CDN = "cdnLogKafka";
    public static final String RUNTIME_PROPERTIES_KAFKA_WAF = "wafLogKafka";
    public static final String ALI_KAFKA_BOOTSTRAP_SERVERS = "AliKafkaBootstrapServers";
    public static final String KAFKA_SOURCE_TOPIC_KEY = "KafkaSourceTopic";
    public static final String KAFKA_CONSUMER_GROUP_ID_KEY = "KafkaConsumerGroupId";
    public static final String TRUSTSTORE_S3_BUCKET_KEY = "TruststoreS3Bucket";
    public static final String TRUSTSTORE_S3_PATH_KEY = "TruststoreS3Path";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    public static Properties getCdnLogKafkaProperties(Map<String, Properties> applicationProperties) throws IOException {
        // note: this won't work when running locally
        Properties cdnLogKafkaProperties = applicationProperties.get(RUNTIME_PROPERTIES_KAFKA_CDN);

        if (cdnLogKafkaProperties == null) {
            LOG.error(LogMsgBuilder.getInstance()
                    .msg("Unable to retrieve FlinkApplicationProperties; please ensure that you've " +
                            "supplied them via application properties.")
                    .build().toString());
            return null;
        }

        if (!cdnLogKafkaProperties.containsKey(ALI_KAFKA_BOOTSTRAP_SERVERS)) {
            LOG.error(LogMsgBuilder.getInstance()
                    .msg("Unable to retrieve property: " + ALI_KAFKA_BOOTSTRAP_SERVERS)
                    .build().toString());
            return null;
        }

        if (!cdnLogKafkaProperties.containsKey(KAFKA_SOURCE_TOPIC_KEY)) {
            LOG.error(LogMsgBuilder.getInstance()
                    .msg("Unable to retrieve property: " + KAFKA_SOURCE_TOPIC_KEY)
                    .build().toString());
            return null;
        }

        if (!cdnLogKafkaProperties.containsKey(KAFKA_CONSUMER_GROUP_ID_KEY)) {
            LOG.error(LogMsgBuilder.getInstance()
                    .msg("Unable to retrieve property: " + KAFKA_CONSUMER_GROUP_ID_KEY)
                    .build().toString());
            return null;
        }

        if (!cdnLogKafkaProperties.containsKey(TRUSTSTORE_S3_BUCKET_KEY)) {
            LOG.error(LogMsgBuilder.getInstance()
                    .msg("Unable to retrieve property: " + TRUSTSTORE_S3_BUCKET_KEY)
                    .build().toString());
            return null;
        }

        if (!cdnLogKafkaProperties.containsKey(TRUSTSTORE_S3_PATH_KEY)) {
            LOG.error(LogMsgBuilder.getInstance()
                    .msg("Unable to retrieve property: " + TRUSTSTORE_S3_PATH_KEY)
                    .build().toString());
            return null;
        }

        if (!cdnLogKafkaProperties.containsKey(USERNAME)) {
            LOG.error(LogMsgBuilder.getInstance()
                    .msg("Unable to retrieve property: " + USERNAME)
                    .build().toString());
            return null;
        }

        if (!cdnLogKafkaProperties.containsKey(PASSWORD)) {
            LOG.error(LogMsgBuilder.getInstance()
                    .msg("Unable to retrieve property: " + PASSWORD)
                    .build().toString());
            return null;
        }


        return cdnLogKafkaProperties;
    }

    public static Properties getWafLogKafkaProperties(Map<String, Properties> applicationProperties) throws IOException {
        Properties wafLogKafkaProperties = applicationProperties.get(RUNTIME_PROPERTIES_KAFKA_WAF);
        if (wafLogKafkaProperties == null) {
            LOG.error(LogMsgBuilder.getInstance()
                    .msg("Unable to retrieve wafLogKafka properties please ensure that you've " +
                            "supplied them via application properties.")
                    .build().toString());
            return null;
        }

        if (!wafLogKafkaProperties.containsKey(ALI_KAFKA_BOOTSTRAP_SERVERS)) {
            LOG.error(LogMsgBuilder.getInstance()
                    .msg("Unable to retrieve property: " + ALI_KAFKA_BOOTSTRAP_SERVERS)
                    .build().toString());
            return null;
        }

        if (!wafLogKafkaProperties.containsKey(KAFKA_SOURCE_TOPIC_KEY)) {
            LOG.error(LogMsgBuilder.getInstance()
                    .msg("Unable to retrieve property: " + KAFKA_SOURCE_TOPIC_KEY)
                    .build().toString());
            return null;
        }

        if (!wafLogKafkaProperties.containsKey(KAFKA_CONSUMER_GROUP_ID_KEY)) {
            LOG.error(LogMsgBuilder.getInstance()
                    .msg("Unable to retrieve property: " + KAFKA_CONSUMER_GROUP_ID_KEY)
                    .build().toString());
            return null;
        }

        if (!wafLogKafkaProperties.containsKey(TRUSTSTORE_S3_BUCKET_KEY)) {
            LOG.error(LogMsgBuilder.getInstance()
                    .msg("Unable to retrieve property: " + TRUSTSTORE_S3_BUCKET_KEY)
                    .build().toString());
            return null;
        }

        if (!wafLogKafkaProperties.containsKey(TRUSTSTORE_S3_PATH_KEY)) {
            LOG.error(LogMsgBuilder.getInstance()
                    .msg("Unable to retrieve property: " + TRUSTSTORE_S3_PATH_KEY)
                    .build().toString());
            return null;
        }

        if (!wafLogKafkaProperties.containsKey(USERNAME)) {
            LOG.error(LogMsgBuilder.getInstance()
                    .msg("Unable to retrieve property: " + USERNAME)
                    .build().toString());
            return null;
        }

        if (!wafLogKafkaProperties.containsKey(PASSWORD)) {
            LOG.error(LogMsgBuilder.getInstance()
                    .msg("Unable to retrieve property: " + PASSWORD)
                    .build().toString());
            return null;
        }
        return wafLogKafkaProperties;
    }
}
