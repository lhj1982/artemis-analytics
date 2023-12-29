package com.nike.artemis.Utils;

import com.nike.artemis.LogMsgBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public class KafkaHelpers {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaHelpers.class);

    private static final String RUNTIME_PROPERTIES_KAFKA_CDN = "cdnLogKafka";
    private static final String RUNTIME_PROPERTIES_KAFKA_WAF = "wafLogKafka";
    public static final String ALI_KAFKA_BOOTSTRAP_SERVERS = "AliKafkaBootstrapServers";
    public static final String KAFKA_SOURCE_TOPIC_KEY = "KafkaSourceTopic";
    public static final String KAFKA_CONSUMER_GROUP_ID_KEY = "KafkaConsumerGroupId";
    public static final String TRUSTSTORE_S3_BUCKET_KEY = "TruststoreS3Bucket";
    public static final String TRUSTSTORE_S3_PATH_KEY = "TruststoreS3Path";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    public static Properties getCdnLogKafkaProperties(Map<String, Properties> applicationProperties) {
        // note: this won't work when running locally
        Properties cdnLogKafkaProperties = applicationProperties.get(RUNTIME_PROPERTIES_KAFKA_CDN);
        if (checkKeys(cdnLogKafkaProperties,
                List.of(ALI_KAFKA_BOOTSTRAP_SERVERS,
                        KAFKA_SOURCE_TOPIC_KEY,
                        KAFKA_CONSUMER_GROUP_ID_KEY,
                        TRUSTSTORE_S3_BUCKET_KEY,
                        TRUSTSTORE_S3_PATH_KEY,
                        USERNAME,
                        PASSWORD)))
            return null;
        return cdnLogKafkaProperties;
    }

    public static Properties getWafLogKafkaProperties(Map<String, Properties> applicationProperties) {
        Properties wafLogKafkaProperties = applicationProperties.get(RUNTIME_PROPERTIES_KAFKA_WAF);
        if (checkKeys(wafLogKafkaProperties,
                List.of(ALI_KAFKA_BOOTSTRAP_SERVERS,
                        KAFKA_SOURCE_TOPIC_KEY,
                        KAFKA_CONSUMER_GROUP_ID_KEY,
                        TRUSTSTORE_S3_BUCKET_KEY,
                        TRUSTSTORE_S3_PATH_KEY,
                        USERNAME,
                        PASSWORD)))
            return null;
        return wafLogKafkaProperties;
    }

    private static boolean checkKeys(Properties kafkaProperties, List<String> keys) {
        if (kafkaProperties == null) {
            LOG.error(LogMsgBuilder.getInstance().msg("Unable to retrieve FlinkApplicationProperties; please ensure that you've " +
                    "supplied them via application properties.").toString());
            return true;
        }

        for (String key : keys) {
            if (!kafkaProperties.containsKey(key)) {
                LOG.error(LogMsgBuilder.getInstance().msg("Unable to retrieve property: " + key).toString());
                return true;
            }
        }
        return false;
    }
}
