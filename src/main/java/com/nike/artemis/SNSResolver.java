package com.nike.artemis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SNSResolver implements FlatMapFunction<String, RequestEvent> {
    public static Logger LOG = LoggerFactory.getLogger(SNSResolver.class);
    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void flatMap(String jsonRequestMessage, Collector<RequestEvent> out) throws Exception {
        LOG.info("before extraction: {}",jsonRequestMessage);
        final RequestEvent requestEvent = objectMapper.readValue(jsonRequestMessage, RequestEvent.class);
        LOG.info("after extraction: {}",requestEvent);
        if (requestEvent.getAddresses() == null){
            LOG.warn("Missing fields: {}", requestEvent);
        }else {
            out.collect(requestEvent);
        }
    }
}
