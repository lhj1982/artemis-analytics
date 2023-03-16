package com.nike.artemis;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonNode;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.util.Collector;

import java.text.SimpleDateFormat;

public class SNSResolver implements FlatMapFunction<String, RequestEvent> {
    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void flatMap(String sns, Collector<RequestEvent> out) throws Exception {

        JsonNode messageNode = objectMapper.readTree(sns);
        String message = messageNode.get("Message").asText();

        String timestamp = messageNode.get("Timestamp").asText();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        long time = simpleDateFormat.parse(timestamp).getTime();

        RequestEvent requestEvent = objectMapper.readValue(message, RequestEvent.class);
        requestEvent.setTimestamp(time);
        out.collect(requestEvent);

    }
}
