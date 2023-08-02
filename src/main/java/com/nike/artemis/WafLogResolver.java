package com.nike.artemis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nike.artemis.Utils.UserIdentifier;
import com.nike.artemis.model.waf.WafData;
import com.nike.artemis.model.waf.WafRequestEvent;
import com.nike.artemis.model.waf.WafUserType;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

public class WafLogResolver implements FlatMapFunction<String, WafRequestEvent> {

    ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public void flatMap(String value, Collector<WafRequestEvent> out) throws Exception {
        WafData wafData = objectMapper.readValue(value, WafData.class);
        Tuple2<WafUserType, String> userInfo =  UserIdentifier.identifyWafUser(wafData);
        out.collect(new WafRequestEvent(wafData.getTime().getTime(), userInfo.f0, userInfo.f1, wafData.getRequest_method(), wafData.getRequest_path()));
    }
}
