package com.nike.artemis.dataResolver;

import com.nike.artemis.model.waf.WafRequestEvent;
import com.nike.artemis.model.waf.WafUserType;
import org.apache.flink.api.common.functions.util.ListCollector;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class WafLogResolverTest {

    @Test
    public void parseValidWafLog_IP() {
        WafLogResolver wafLogResolver = new WafLogResolver(null);
        List<WafRequestEvent> out = new ArrayList<>();
        ListCollector<WafRequestEvent> collector = new ListCollector<>(out);

        String wafLog = "{\n" +
                "    \"__source__\": \"xlogc\",\n" +
                "    \"__time__\": \"1690858257\",\n" +
                "    \"__topic__\": \"waf_access_log\",\n" +
                "    \"algorithm_rule_id\": \"9367794\",\n" +
                "    \"block_action\": \"\",\n" +
                "    \"body_bytes_sent\": \"410\",\n" +
                "    \"bypass_matched_ids\": \"2986711\",\n" +
                "    \"content_type\": \"-\",\n" +
                "    \"host\": \"auth.prod.commerce.nikecloud.com.cn\",\n" +
                "    \"http_cookie\": \"acw_sc__v2=64c870bb070705f8a040aeae9f95582f3533802c; acw_tc=6a74ab9116908576592631932e6ff1836884827f8c905ca2b7138fa485\",\n" +
                "    \"http_referer\": \"-\",\n" +
                "    \"http_user_agent\": \"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36\",\n" +
                "    \"http_x_forwarded_for\": \"36.103.243.169, 10.120.24.132\",\n" +
                "    \"https\": \"on\",\n" +
                "    \"intelligence_action\": \"block\",\n" +
                "    \"intelligence_rule_id\": \"7137033\",\n" +
                "    \"intelligence_test\": \"true\",\n" +
                "    \"matched_host\": \"auth.prod.commerce.nikecloud.com.cn\",\n" +
                "    \"querystring\": \"-\",\n" +
                "    \"real_client_ip\": \"36.103.243.169\",\n" +
                "    \"region\": \"cn\",\n" +
                "    \"remote_addr\": \"106.116.171.132\",\n" +
                "    \"remote_port\": \"57388\",\n" +
                "    \"request_body\": \"-\",\n" +
                "    \"request_length\": \"2660\",\n" +
                "    \"request_method\": \"GET\",\n" +
                "    \"request_path\": \"/buy/checkouts_jobs/v3/a85f2e46-103c-4b27-8f95-93cc970b910e\",\n" +
                "    \"request_time_msec\": \"98\",\n" +
                "    \"request_traceid\": \"6a74ab9116908582577722188e\",\n" +
                "    \"response_set_cookie\": \"nil\",\n" +
                "    \"scene_action\": \"js_pass\",\n" +
                "    \"scene_id\": \"88df2d30_da20_4185_b869_ca9b323265b0\",\n" +
                "    \"scene_rule_id\": \"9368195\",\n" +
                "    \"scene_rule_type\": \"js-88df2d30_da20_4185_b869_ca9b323265b0\",\n" +
                "    \"scene_test\": \"false\",\n" +
                "    \"server_port\": \"443\",\n" +
                "    \"server_protocol\": \"HTTP/1.1\",\n" +
                "    \"ssl_cipher\": \"ECDHE-RSA-AES128-GCM-SHA256\",\n" +
                "    \"ssl_protocol\": \"TLSv1.2\",\n" +
                "    \"status\": \"200\",\n" +
                "    \"time\": \"2023-08-01T10:50:57+08:00\",\n" +
                "    \"upstream_addr\": \"69.231.177.3:443\",\n" +
                "    \"upstream_response_time\": \"0.096\",\n" +
                "    \"upstream_status\": \"200\",\n" +
                "    \"user_id\": \"1759439040238926\",\n" +
                "    \"wxbb_info_tbl\": \"{\\\"new\\\":\\\"\\\",\\\"reason\\\":\\\"wToken header not found\\\",\\\"umid\\\":\\\"\\\"}\"\n" +
                "}";
        wafLogResolver.flatMap(wafLog, collector);
        Assert.assertEquals("36.103.243.169", out.get(0).getUser());
        Assert.assertEquals(WafUserType.ipaddress, out.get(0).getUserType());
    }


    @Test
    public void parseValidWafLog_Umid() {
        WafLogResolver wafLogResolver = new WafLogResolver(null);
        List<WafRequestEvent> out = new ArrayList<>();
        ListCollector<WafRequestEvent> collector = new ListCollector<>(out);

        String wafLog = "{\n" +
                "    \"__source__\": \"xlogc\",\n" +
                "    \"__time__\": \"1694684473\",\n" +
                "    \"__topic__\": \"waf_access_log\",\n" +
                "    \"acl_action\": \"block\",\n" +
                "    \"acl_rule_id\": \"8790391\",\n" +
                "    \"acl_rule_type\": \"custom\",\n" +
                "    \"acl_test\": \"true\",\n" +
                "    \"block_action\": \"\",\n" +
                "    \"body_bytes_sent\": \"2851\",\n" +
                "    \"content_type\": \"application/json\",\n" +
                "    \"host\": \"auth.prod.commerce.nikecloud.com.cn\",\n" +
                "    \"http_cookie\": \"acw_tc=7793292016946844674353883e338568652624ac4d526567914d8f3bd5; cdn_sec_tc=7793292016946844674353883e338568652624ac4d526567914d8f3bd5; mp_56fa4377ba1b82a4378b4798_mixpanel=%7B%22distinct_id%22%3A%20%221884da463ae29d-02bbbf9d1cc60c8-774c1551-505c8-1884da463af24bf%22%7D\",\n" +
                "    \"http_referer\": \"-\",\n" +
                "    \"http_user_agent\": \"NikeApp/23.42.1 (prod; 2308312223; iOS 16.2; iPhone14,2)\",\n" +
                "    \"http_x_forwarded_for\": \"119.143.212.151, 10.120.24.41\",\n" +
                "    \"https\": \"on\",\n" +
                "    \"matched_host\": \"auth.prod.commerce.nikecloud.com.cn\",\n" +
                "    \"querystring\": \"?filter=orderType%28SALES_ORDER%29\\u0026sort=orderSubmitDateDesc\\u0026anchor=0\\u0026count=10\\u0026country=CN\\u0026language=zh-Hans\",\n" +
                "    \"real_client_ip\": \"119.143.212.151\",\n" +
                "    \"region\": \"cn\",\n" +
                "    \"remote_addr\": \"119.147.41.31\",\n" +
                "    \"remote_port\": \"21861\",\n" +
                "    \"request_body\": \"client_id=e7cc33c015ad3360f16b967b692ea595&refresh_token=eyJhbGciOiJSUzI1NiIsImtpZCI6IjgyNzIxYjVkLTQwMTAtNDVkOC04ZmYxLTNjZDNhZTY0YTMwMXNpZyJ9.eyJpYXQiOjE3MTMzNzA1MDcsImV4cCI6MTc0NDkwNjUwNywiaXNzIjoib2F1dGgyaWR0LWNuIiwianRpIjoiZDQ5YzMwNDItZDZjNi00Yjc2LWE1ZjMtMDFmZDIzODZkMTAzIiwiYXVkIjoib2F1dGgyaWR0IiwidHJ1c3QiOjEwMCwibGF0IjoxNzEzMjg0NDg0LCJjbGkiOiJlN2NjMzNjMDE1YWQzMzYwZjE2Yjk2N2I2OTJlYTU5NSIsInN1YiI6ImJhY2Q4NDQ0LTZhNTEtNGVhMS05NTg1LTY5MWEzMzE3M2U1ZCIsInNidCI6Im5pa2U6cGx1cyIsInNjcCI6WyJuaWtlLmRpZ2l0YWwiXSwicnNpZCI6IjM4OGRlMDkyLThjZDUtNDRjOS1iYzM2LTc2NDJmMjgzZGU3ZCIsImxyc2NwIjoib3BlbmlkIHByb2ZpbGUgZW1haWwgcGhvbmUgZmxvdyBvZmZsaW5lX2FjY2VzcyBuaWtlLmRpZ2l0YWwifQ.luIElAAMtuX9enTOMurp33rfhj0zfncAel8XARE_NcvEsGryr6_qxTKDhfHdwO7TLTK1YiYHYNVSomBiDWQ4t-XDXhQ7eYft-GscVHGl3357-O4E9t10MbAY9XA5bNjnbGi4MY7HAsw51vpa1ttaOkagUw4Ei2QQne1pgKXdl8lt9xHEnM6voq6V-lSr2eJWC7tyvbVKvDiaWVNXDhpW-SuHCt45VN6YD-VOXs6Kly8imHCS8fAiqAVlpUxTehk3XahvlGTNsS1-nvAZhNz_yaTrxZbwX0Al9swtyCHgbMRh-Dlz4MOi0WRxu7_l3o30r-pPwXMxw46kLwxBJMgOww&grant_type=refresh_token\",\n" +
                "    \"request_length\": \"3926\",\n" +
                "    \"request_method\": \"GET\",\n" +
                "    \"request_path\": \"/orders/history/v1\",\n" +
                "    \"request_time_msec\": \"469\",\n" +
                "    \"request_traceid\": \"7793292016946844725276866e\",\n" +
                "    \"response_set_cookie\": \"nil\",\n" +
                "    \"server_port\": \"443\",\n" +
                "    \"server_protocol\": \"HTTP/1.1\",\n" +
                "    \"ssl_cipher\": \"ECDHE-RSA-AES128-GCM-SHA256\",\n" +
                "    \"ssl_protocol\": \"TLSv1.2\",\n" +
                "    \"status\": \"200\",\n" +
                "    \"time\": \"2023-09-14T17:41:13+08:00\",\n" +
                "    \"upstream_addr\": \"52.83.188.205:443\",\n" +
                "    \"upstream_response_time\": \"0.465\",\n" +
                "    \"upstream_status\": \"200\",\n" +
                "    \"user_id\": \"1759439040238926\",\n" +
                "    \"wxbb_info_tbl\": {\"timestamp\":\"1715217799330\",\"umid\":\"FC4DADB0-403B-4104-B110-11657589417\",\"unicorn\":\"0\"}\n" +
                "}";
        wafLogResolver.flatMap(wafLog, collector);
        Assert.assertEquals("FC4DADB0-403B-4104-B110-11657589417", out.get(0).getUser());
        Assert.assertEquals(WafUserType.umid, out.get(0).getUserType());
    }

    @Test
    public void doesNotFail_whenEmptyWafLog() {
        WafLogResolver wafLogResolver = new WafLogResolver(null);
        List<WafRequestEvent> out = new ArrayList<>();
        ListCollector<WafRequestEvent> collector = new ListCollector<>(out);

        wafLogResolver.flatMap("", collector);
        Assert.assertEquals(0, out.size());
    }



}
