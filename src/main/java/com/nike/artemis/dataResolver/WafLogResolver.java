package com.nike.artemis.dataResolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nike.artemis.Utils.UserIdentifier;
import com.nike.artemis.model.waf.WafData;
import com.nike.artemis.model.waf.WafRequestEvent;
import com.nike.artemis.model.waf.WafUserType;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WafLogResolver implements FlatMapFunction<String, WafRequestEvent> {

    public static Logger LOG = LoggerFactory.getLogger(WafLogResolver.class);
    ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public void flatMap(String wafLog, Collector<WafRequestEvent> out) {
        WafData wafData = null;
        try {
            wafData = objectMapper.readValue(wafLog, WafData.class);
            Tuple2<WafUserType, String> userInfo =  UserIdentifier.identifyWafUser(wafData);
            out.collect(new WafRequestEvent(wafData.getTime().getTime(), userInfo.f0, userInfo.f1, wafData.getRequest_method(), wafData.getRequest_path(),wafData.getStatus()));
        } catch (Exception e) {
            LOG.error("Location={WafLogResolver} source={} error={}", wafLog, e);
        }
    }
}
