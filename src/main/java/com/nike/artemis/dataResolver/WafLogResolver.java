package com.nike.artemis.dataResolver;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nike.artemis.LogMsgBuilder;
import com.nike.artemis.Utils.UserIdentifier;
import com.nike.artemis.model.AccountType;
import com.nike.artemis.model.waf.WafData;
import com.nike.artemis.model.waf.WafRequestEvent;
import com.nike.artemis.model.waf.WafUserType;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

public class WafLogResolver implements FlatMapFunction<String, WafRequestEvent> {

    public static Logger LOG = LoggerFactory.getLogger(WafLogResolver.class);
    private final List<String> requestPaths;
    ObjectMapper objectMapper = new ObjectMapper();

    public WafLogResolver(List<String> requestPaths) {
        this.requestPaths = requestPaths;
    }

    @Override
    public void flatMap(String wafLog, Collector<WafRequestEvent> out) {
        LOG.info(LogMsgBuilder.getInstance()
                .source(WafRequestEvent.class.getSimpleName())
                .msg(String.format("logs from Ali cloud WAF kafka: %s", wafLog)).toString());

        WafData wafData;
        try {
//            wafData = objectMapper.readValue(wafLog, WafData.class);
            JsonNode jsonNode = objectMapper.readTree(wafLog);
            wafData = this.getWafData(jsonNode);
            Tuple2<WafUserType, Tuple3<String, String, String>> userInfo = UserIdentifier.identifyWafUser(this.requestPaths, wafData);
            if (Objects.equals(userInfo.f1.f1, AccountType.PLUS.getType())) {
                out.collect(new WafRequestEvent(wafData.getTime(), userInfo.f0, userInfo.f1.f0,
                        wafData.getRequest_method(), wafData.getRequest_path(), wafData.getStatus(), userInfo.f1.f2));
            }
        } catch (Exception e) {
            LOG.error(LogMsgBuilder.getInstance()
                    .source(WafRequestEvent.class.getSimpleName())
                    .msg("resolve waf data from kafka failed")
                    .data(wafLog)
                    .exception(e.getMessage()).toString());
        }
    }

    private WafData getWafData(JsonNode jsonNode) {
        WafData wafData = new WafData();
        wafData.setHost(jsonNode.get("host").asText());
        wafData.setHttp_user_agent(jsonNode.get("http_user_agent").asText());
        wafData.setHttps(jsonNode.get("https").asText());
        wafData.setHttp_cookie(jsonNode.get("http_cookie").asText());
        wafData.setReal_client_ip(jsonNode.get("real_client_ip").asText());
        wafData.setStatus(jsonNode.get("status").asText());
        wafData.setRequest_method(jsonNode.get("request_method").asText());
        wafData.setRequest_body(jsonNode.get("request_body").asText());
        wafData.setRequest_path(jsonNode.get("request_path").asText());
        wafData.setRequest_traceid(jsonNode.get("request_traceid").asText());

        String time = jsonNode.get("time").asText();
        LocalDateTime dateTime = LocalDateTime.parse(time.replace("+08:00", ""));
        ZonedDateTime zonedDateTime = dateTime.atZone(ZoneId.of("+08:00"));
        Instant instant = zonedDateTime.toInstant();
        wafData.setTime(instant.toEpochMilli());

        wafData.setUser_id(jsonNode.get("user_id").asText());
        wafData.setWxbb_info_tbl(jsonNode.get("wxbb_info_tbl").toString());

        return wafData;
    }
}
