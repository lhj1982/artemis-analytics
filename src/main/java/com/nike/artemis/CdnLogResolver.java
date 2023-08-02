package com.nike.artemis;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.nike.artemis.Utils.UserIdentifier;
import com.nike.artemis.model.cdn.CdnData;
import com.nike.artemis.model.cdn.CdnRequestEvent;
import com.nike.artemis.model.cdn.CdnUserType;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

public class CdnLogResolver implements FlatMapFunction<String, CdnRequestEvent> {

    ObjectMapper objectMapper = JsonMapper.builder().enable(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER).build();
    @Override
    public void flatMap(String value, Collector<CdnRequestEvent> out) throws Exception {
        CdnData cdnData = objectMapper.readValue(value, CdnData.class);
        Tuple2<CdnUserType, String> userType = UserIdentifier.identifyCdnUser(cdnData);
        out.collect(new CdnRequestEvent(cdnData.getUnixtime(),userType.f0.name(), userType.f1, cdnData.getMethod(), cdnData.getUri()));
    }
}
