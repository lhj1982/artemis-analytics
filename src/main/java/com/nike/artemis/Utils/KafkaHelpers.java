package com.nike.artemis.Utils;

import com.amazonaws.services.kinesisanalytics.runtime.KinesisAnalyticsRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class KafkaHelpers {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaHelpers.class);

    public static final String ALI_KAFKA_BOOTSTRAP_SERVERS = "AliKafkaBootstrapServers";
    public static final String KAFKA_SOURCE_TOPIC_KEY = "KafkaSourceTopic";
    public static final String KAFKA_CONSUMER_GROUP_ID_KEY = "KafkaConsumerGroupId";
    public static final String TRUSTSTORE_S3_BUCKET_KEY = "TruststoreS3Bucket";
    public static final String TRUSTSTORE_S3_PATH_KEY = "TruststoreS3Path";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    public static Properties getCdnLogKafkaProperties() throws IOException {
        // note: this won't work when running locally
        Map<String, Properties> applicationProperties = KinesisAnalyticsRuntime.getApplicationProperties();
        Properties cdnLogKafkaProperties = applicationProperties.get("cdnLogKafka");

        if(cdnLogKafkaProperties == null) {
            LOG.error("Unable to retrieve FlinkApplicationProperties; please ensure that you've " +
                    "supplied them via application properties.");
            return null;
        }

        if(!cdnLogKafkaProperties.containsKey(ALI_KAFKA_BOOTSTRAP_SERVERS)) {
            LOG.error("Unable to retrieve property: " + ALI_KAFKA_BOOTSTRAP_SERVERS);
            return null;
        }

        if(!cdnLogKafkaProperties.containsKey(KAFKA_SOURCE_TOPIC_KEY)) {
            LOG.error("Unable to retrieve property: " + KAFKA_SOURCE_TOPIC_KEY);
            return null;
        }

        if(!cdnLogKafkaProperties.containsKey(KAFKA_CONSUMER_GROUP_ID_KEY)) {
            LOG.error("Unable to retrieve property: " + KAFKA_CONSUMER_GROUP_ID_KEY);
            return null;
        }

        if(!cdnLogKafkaProperties.containsKey(TRUSTSTORE_S3_BUCKET_KEY)) {
            LOG.error("Unable to retrieve property: " + TRUSTSTORE_S3_BUCKET_KEY);
            return null;
        }

        if(!cdnLogKafkaProperties.containsKey(TRUSTSTORE_S3_PATH_KEY)) {
            LOG.error("Unable to retrieve property: " + TRUSTSTORE_S3_PATH_KEY);
            return null;
        }

        if(!cdnLogKafkaProperties.containsKey(USERNAME)) {
            LOG.error("Unable to retrieve property: " + USERNAME);
            return null;
        }

        if(!cdnLogKafkaProperties.containsKey(PASSWORD)) {
            LOG.error("Unable to retrieve property: " + PASSWORD);
            return null;
        }


        return cdnLogKafkaProperties;
    }
    public static Properties getWafLogKafkaProperties() throws IOException {
        Map<String, Properties> applicationProperties = KinesisAnalyticsRuntime.getApplicationProperties();
        Properties wafLogKafkaProperties = applicationProperties.get("wafLogKafka");
        if(wafLogKafkaProperties == null) {
            LOG.error("Unable to retrieve wafLogKafka properties please ensure that you've " +
                    "supplied them via application properties.");
            return null;
        }

        if(!wafLogKafkaProperties.containsKey(ALI_KAFKA_BOOTSTRAP_SERVERS)) {
            LOG.error("Unable to retrieve property: " + ALI_KAFKA_BOOTSTRAP_SERVERS);
            return null;
        }

        if(!wafLogKafkaProperties.containsKey(KAFKA_SOURCE_TOPIC_KEY)) {
            LOG.error("Unable to retrieve property: " + KAFKA_SOURCE_TOPIC_KEY);
            return null;
        }

        if(!wafLogKafkaProperties.containsKey(KAFKA_CONSUMER_GROUP_ID_KEY)) {
            LOG.error("Unable to retrieve property: " + KAFKA_CONSUMER_GROUP_ID_KEY);
            return null;
        }

        if(!wafLogKafkaProperties.containsKey(TRUSTSTORE_S3_BUCKET_KEY)) {
            LOG.error("Unable to retrieve property: " + TRUSTSTORE_S3_BUCKET_KEY);
            return null;
        }

        if(!wafLogKafkaProperties.containsKey(TRUSTSTORE_S3_PATH_KEY)) {
            LOG.error("Unable to retrieve property: " + TRUSTSTORE_S3_PATH_KEY);
            return null;
        }

        if(!wafLogKafkaProperties.containsKey(USERNAME)) {
            LOG.error("Unable to retrieve property: " + USERNAME);
            return null;
        }

        if(!wafLogKafkaProperties.containsKey(PASSWORD)) {
            LOG.error("Unable to retrieve property: " + PASSWORD);
            return null;
        }
        return wafLogKafkaProperties;
    }
}
