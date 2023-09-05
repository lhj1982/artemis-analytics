package com.nike.artemis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.nike.artemis.Utils.UserIdentifier;
import com.nike.artemis.model.cdn.CdnData;
import com.nike.artemis.model.cdn.CdnRequestEvent;
import com.nike.artemis.model.cdn.CdnUserType;
import org.apache.flink.api.java.tuple.Tuple2;

public class CdnLogResolverTest {
    public static void main(String[] args) throws JsonProcessingException {
        String data="{\n" +
                "\t\"unixtime\": \"1692826098\",\n" +
                "\t\"uuid\": \"de4b3f9916928260980213883e\",\n" +
                "\t\"user_info\": \"accEnc=gzip, deflate||accLang=-||accRange=-||age=-||allow=*||allowOrigin=https://www.nike.com.cn||auth=-||cacheCtl=-||conn=keep-alive||contDisp=-||contEnc=gzip||contLang=en-||contMD5=-||date=Wed, 23 Aug 2023 21:28:18 GMT||DNT=-||edgeControl=-||eTag=-||expect=-||expires=-||fwdHost=-||ifMatch=-||ifMod=-||ifNone=-||ifRange=-||ifUnmod=-||lastMod=-||link=-||p3p=-||parentSpanId=-||protoVer=HTTP/1.1||redirURL=-||respCacheCtl=no-store||respConn=close||respContMD5=-||respCT=application/json||retry=-||sampleable=-||server=-||set_cookie=-||spanId=-||spanName=-||te=-||traceId_in=-||traceId_out=bf423a3eb570a041||trailer=-||transEnc=chunked||upgrade=-||vary=-||warning=-||wwwAuth=-||xPwrdBy=-||xReqWith=-||zhenghe_count=14151||zhenghe_githash=-||zhenghe_target_host=public.test.commerce.origin.nike.com.cn||zhenghe_timing_ms=188||zhenghe_version=v6.8.1||zhenghe_bm_result=0067||ip_region_en=Ningxia||ip_country_en=China||ip_city_en=Zhongwei\",\n" +
                "\t\"client_ip\": \"161.189.161.100\",\n" +
                "\t\"user_agent\": \"snkrs-content/v2\",\n" +
                "\t\"method\": \"GET\",\n" +
                "\t\"return_code\": \"400\",\n" +
                "\t\"uri\": \"/product_feed/threads/v3\"\n" +
                "}";
        ObjectMapper objectMapper = JsonMapper.builder().enable(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER).build();
        CdnData cdnData = objectMapper.readValue(data, CdnData.class);
        Tuple2<CdnUserType, String> userType = UserIdentifier.identifyCdnUser(cdnData);
        System.out.println(userType);
        CdnRequestEvent event = new CdnRequestEvent(cdnData.getUnixtime(), userType.f0.name(), userType.f1, cdnData.getMethod(), cdnData.getUri());
        System.out.println(event);


    }
}
