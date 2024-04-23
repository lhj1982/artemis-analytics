package com.nike.artemis.dataResolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nike.artemis.LogMsgBuilder;
import com.nike.artemis.Utils.UserIdentifier;
import com.nike.artemis.model.AccountType;
import com.nike.artemis.model.waf.WafData;
import com.nike.artemis.model.waf.WafRequestEvent;
import com.nike.artemis.model.waf.WafUserType;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        LOG.debug(LogMsgBuilder.getInstance()
                .source(WafRequestEvent.class.getSimpleName())
                .msg(String.format("logs from Ali cloud WAF kafka: %s", wafLog)).toString());

        WafData wafData = null;
        try {
            wafData = objectMapper.readValue(wafLog, WafData.class);
            Tuple2<WafUserType, Tuple2<String, String>> userInfo = UserIdentifier.identifyWafUser(this.requestPaths, wafData);
            if (Objects.equals(userInfo.f1.f1, AccountType.PLUS.getType())) {
                out.collect(new WafRequestEvent(wafData.getTime().getTime(), userInfo.f0, userInfo.f1.f0,
                        wafData.getRequest_method(), wafData.getRequest_path(), wafData.getStatus()));
            }
        } catch (Exception e) {
            LOG.error(LogMsgBuilder.getInstance()
                    .source(WafRequestEvent.class.getSimpleName())
                    .msg("resolve waf data from kafka failed")
                    .data(wafLog)
                    .exception(e.getMessage()).toString());
        }
    }
}
