package com.nike.artemis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

public class SNSResolver implements FlatMapFunction<String, RequestEvent> {
    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void flatMap(String jsonRequestMessage, Collector<RequestEvent> out) throws Exception {

        final RequestEvent requestEvent = objectMapper.readValue(jsonRequestMessage, RequestEvent.class);
        out.collect(requestEvent);

    }
}
