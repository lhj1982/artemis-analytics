package com.nike.artemis.dataResolver;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.nike.artemis.LogMsgBuilder;
import com.nike.artemis.Utils.UserIdentifier;
import com.nike.artemis.model.AccountType;
import com.nike.artemis.model.cdn.CdnData;
import com.nike.artemis.model.cdn.CdnRequestEvent;
import com.nike.artemis.model.cdn.CdnUserType;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

public class CdnLogResolver implements FlatMapFunction<String, CdnRequestEvent> {
    public static Logger LOG = LoggerFactory.getLogger(CdnLogResolver.class);
    ObjectMapper objectMapper = JsonMapper.builder().enable(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER).build();

    @Override
    public void flatMap(String cdnLog, Collector<CdnRequestEvent> out) {
        LOG.debug(LogMsgBuilder.getInstance()
                .source(CdnRequestEvent.class.getSimpleName())
                .msg(String.format("logs from Ali cloud CDN kafka: %s,arrivalTime :%s", cdnLog,
                        LocalDateTime.now().toInstant(ZoneOffset.ofHours(0)).toEpochMilli())).toString());

        CdnData cdnData = null;
        try {
            cdnData = objectMapper.readValue(cdnLog, CdnData.class);

            if (Long.toString(cdnData.getUnixtime()).length() == 10) {
                cdnData.setUnixtime(cdnData.getUnixtime() * 1000L);
            }
            if (Objects.nonNull(cdnData.getSls_receive_time()) && Long.toString(cdnData.getSls_receive_time()).length() == 10) {
                cdnData.setSls_receive_time(cdnData.getSls_receive_time() * 1000L);
            }
            Tuple2<CdnUserType, Tuple2<String, String>> userType = UserIdentifier.identifyCdnUser(cdnData);
            if (Objects.equals(userType.f1.f1, AccountType.PLUS.getType())) {
                out.collect(new CdnRequestEvent(cdnData.getUnixtime(), userType.f0.name(), userType.f1.f0, cdnData.getMethod(),
                        cdnData.getUri(), cdnData.getReturn_code(), cdnData.getSls_receive_time()));
            }
        } catch (Exception e) {
            LOG.error(LogMsgBuilder.getInstance()
                    .source(CdnRequestEvent.class.getSimpleName())
                    .msg("resolve cdn data from kafka failed")
                    .data(cdnLog)
                    .exception(e.getMessage()).toString());
        }

    }
}
