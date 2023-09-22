package com.nike.artemis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class SNSResolver implements FlatMapFunction<String, RequestEvent> {
    public static Logger LOG = LoggerFactory.getLogger(SNSResolver.class);
    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void flatMap(String jsonRequestMessage, Collector<RequestEvent> out) {
        if (jsonRequestMessage.length() == 0)
            return;

        LOG.info("before extraction: {}, current_time:{}",jsonRequestMessage, LocalDateTime.now().toInstant(ZoneOffset.ofHours(0)).toEpochMilli());
        final RequestEvent requestEvent;
        try {
            requestEvent = objectMapper.readValue(jsonRequestMessage, RequestEvent.class);
            if (requestEvent.getAddresses() == null || requestEvent.experience.launchId == null || requestEvent.user.upmId == null || requestEvent.device.trueClientIp == null){
                LOG.warn("Missing fields: {}", requestEvent);
            }else {
                out.collect(requestEvent);
            }
        } catch (JsonProcessingException e) {
            LOG.error("type={SNSResolver} source={} error={}", jsonRequestMessage, e);
        }

    }
}
