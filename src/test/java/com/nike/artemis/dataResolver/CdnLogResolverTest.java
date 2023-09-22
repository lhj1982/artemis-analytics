package com.nike.artemis;

import com.nike.artemis.dataResolver.CdnLogResolver;
import com.nike.artemis.model.cdn.CdnRequestEvent;
import org.apache.flink.api.common.functions.util.ListCollector;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class CdnLogResolverTest {

    @Test
    public void parseValidCdnLogUpmid() {
        CdnLogResolver cdnLogResolver = new CdnLogResolver();
        List<CdnRequestEvent> out = new ArrayList<>();
        ListCollector<CdnRequestEvent> collector = new ListCollector<>(out);

        String cdnLog = "{\n" +
                "\t\"unixtime\": \"1694653776\",\n" +
                "\t\"uuid\": \"6a0fda9616946537764337109e\",\n" +
                "\t\"user_info\": \"accEnc=br;q=1.0, gzip;q=0.9, deflate;q=0.8||accLang=zh-Hans-CN;q=1.0, wuu-Hans-CN;q=0.9, en-IN;q=0.8||accRange=-||age=-||allow=GET,POST,OPTIONS,PUT,DELETE||allowOrigin=*||auth=Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6IjYxODJhYjdmLWQxYTMtNDY4OS05YTgzLTJhMjI1ODNmOGUwMXNpZyJ9.eyJpYXQiOjE2OTQ2NTI3NTgsImV4cCI6MTY5NDY1NjM1OCwiaXNzIjoib2F1dGgyYWNjLWNuIiwianRpIjoiZjQ4ZDIzMjctYTQ0Mi00ZDYyLTljNjUtNWQwMmNiNGYzZDFhIiwiYXVkIjoiY29tLm5pa2UuZGlnaXRhbCIsInNidCI6Im5pa2U6YXBwIiwidHJ1c3QiOjEwMCwibGF0IjoxNjkxMzgyMjQ0LCJzY3AiOlsibmlrZS5kaWdpdGFsIl0sInN1YiI6ImNvbS5uaWtlLmNvbW1lcmNlLm9tZWdhLmlvcyIsInBybiI6ImVhOWI1ZjgxLTY5MTgtNDVkOC1hODJiLTlhZTc4MGQ2ZjhkZSIsInBydCI6Im5pa2U6cGx1cyIsImxyc2NwIjoib3BlbmlkIHByb2ZpbGUgZW1haWwgcGhvbmUgZmxvdyBvZmZsaW5lX2FjY2VzcyBuaWtlLmRpZ2l0YWwifQ.e0luHqRczKIkFbFiIpWDUycBX2Lq2foIMDoQsful652QdfRYTtDV2GUYKpUCjhmuh0AjxpMxvTO54qsC61uwuuhMAI5-vgjMKbKpaB7ii_DVRdyrKkPUTvRYMO-ZspCFyPae0BHNcV6iCwpGfAZ7uG7OVDr5w1pEqexXRFZNHQCRcFsbjRSmMYKkaQpDiyL9mKATtAhPzABUl1hYzIILyrx5glucR_vN8rNJ0kN2PpE-T03PK5Ht81H_gH9-qlobvWdLS21N2UIE4TSEREMEfjgcWIlOsCajUKqhwpTT5_slTF0YqME1z9uwWxpaz8YXe-axa3fakQntqc32AGfoIQ||cacheCtl=-||conn=-||contDisp=-||contEnc=-||contLang=-||contMD5=-||date=Thu, 14 Sep 2023 01:09:36 GMT||DNT=-||edgeControl=-||eTag=-||expect=-||expires=-||fwdHost=-||ifMatch=-||ifMod=-||ifNone=-||ifRange=-||ifUnmod=-||lastMod=-||link=-||p3p=-||parentSpanId=-||protoVer=HTTP/2.0||redirURL=-||respCacheCtl=-||respConn=close||respContMD5=-||respCT=application/json;charset=UTF-8||retry=-||sampleable=-||server=-||set_cookie=-||spanId=-||spanName=-||te=-||traceId_in=feff33e0c5fe82b8||traceId_out=3bdeb671258bb45e||trailer=-||transEnc=-||upgrade=-||vary=-||warning=-||wwwAuth=-||xPwrdBy=-||xReqWith=-||zhenghe_count=578044||zhenghe_githash=-||zhenghe_target_host=auth.prod.commerce.origin.nike.com.cn||zhenghe_timing_ms=12||zhenghe_version=v6.10.1||zhenghe_bm_result=-||ip_region_en=Shanghai||ip_country_en=China||ip_city_en=Shanghai\",\n" +
                "\t\"client_ip\": \"220.196.194.29\",\n" +
                "\t\"user_agent\": \"NikeApp/23.42.1 (prod; 2308312223; iOS 16.6.1; iPhone14,3)\",\n" +
                "\t\"method\": \"PUT\",\n" +
                "\t\"return_code\": \"202\",\n" +
                "\t\"uri\": \"/buy/checkout_previews/v3/9a05509e-0343-495b-9a36-42378bb58194\"\n" +
                "}";
        cdnLogResolver.flatMap(cdnLog, collector);
        Assert.assertEquals("ea9b5f81-6918-45d8-a82b-9ae780d6f8de", out.get(0).getUser());
    }

    @Test
    public void parseValidCdnLogIp() {
        CdnLogResolver cdnLogResolver = new CdnLogResolver();
        List<CdnRequestEvent> out = new ArrayList<>();
        ListCollector<CdnRequestEvent> collector = new ListCollector<>(out);
        String cdnLog = "{\n" +
                "\t\"unixtime\": \"1694653776\",\n" +
                "\t\"uuid\": \"6a0fda9616946537764337109e\",\n" +
                "\t\"user_info\": \"accEnc=br;q=1.0, gzip;q=0.9, deflate;q=0.8||accLang=zh-Hans-CN;q=1.0, wuu-Hans-CN;q=0.9, en-IN;q=0.8||accRange=-||age=-||allow=GET,POST,OPTIONS,PUT,DELETE||allowOrigin=*||auth=-||cacheCtl=-||conn=-||contDisp=-||contEnc=-||contLang=-||contMD5=-||date=Thu, 14 Sep 2023 01:09:36 GMT||DNT=-||edgeControl=-||eTag=-||expect=-||expires=-||fwdHost=-||ifMatch=-||ifMod=-||ifNone=-||ifRange=-||ifUnmod=-||lastMod=-||link=-||p3p=-||parentSpanId=-||protoVer=HTTP/2.0||redirURL=-||respCacheCtl=-||respConn=close||respContMD5=-||respCT=application/json;charset=UTF-8||retry=-||sampleable=-||server=-||set_cookie=-||spanId=-||spanName=-||te=-||traceId_in=feff33e0c5fe82b8||traceId_out=3bdeb671258bb45e||trailer=-||transEnc=-||upgrade=-||vary=-||warning=-||wwwAuth=-||xPwrdBy=-||xReqWith=-||zhenghe_count=578044||zhenghe_githash=-||zhenghe_target_host=auth.prod.commerce.origin.nike.com.cn||zhenghe_timing_ms=12||zhenghe_version=v6.10.1||zhenghe_bm_result=-||ip_region_en=Shanghai||ip_country_en=China||ip_city_en=Shanghai\",\n" +
                "\t\"client_ip\": \"220.196.194.29\",\n" +
                "\t\"user_agent\": \"NikeApp/23.42.1 (prod; 2308312223; iOS 16.6.1; iPhone14,3)\",\n" +
                "\t\"method\": \"PUT\",\n" +
                "\t\"return_code\": \"202\",\n" +
                "\t\"uri\": \"/buy/checkout_previews/v3/9a05509e-0343-495b-9a36-42378bb58194\"\n" +
                "}";
        cdnLogResolver.flatMap(cdnLog, collector);
        Assert.assertEquals("220.196.194.29", out.get(0).getUser());
    }

    @Test
    public void doesNotFail_whenEmptyCdnLog() {
        CdnLogResolver cdnLogResolver = new CdnLogResolver();
        List<CdnRequestEvent> out = new ArrayList<>();
        ListCollector<CdnRequestEvent> collector = new ListCollector<>(out);
        cdnLogResolver.flatMap("", collector);
        Assert.assertEquals(0, out.size());
    }
}


