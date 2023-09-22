package com.nike.artemis;

import com.nike.artemis.dataResolver.WafLogResolver;
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
        WafLogResolver wafLogResolver = new WafLogResolver();
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
        WafLogResolver wafLogResolver = new WafLogResolver();
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
                "    \"request_body\": \"-\",\n" +
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
                "    \"wxbb_info_tbl\": \"{\\\"\\\":\\\"1\\\",\\\"abnormal_imei\\\":\\\"0\\\",\\\"abnormal_time\\\":\\\"0\\\",\\\"adbstate\\\":\\\"0\\\",\\\"appname\\\":\\\"NikeAppCN\\\",\\\"appversion\\\":\\\"23.42.1\\\",\\\"batterychange\\\":\\\"0\\\",\\\"batterylevel\\\":\\\"67\\\",\\\"batterystatus\\\":\\\"4\\\",\\\"brand\\\":\\\"apple\\\",\\\"brightness\\\":\\\"60\\\",\\\"btime\\\":\\\"1690412521793\\\",\\\"buildversion\\\":\\\"20C65\\\",\\\"cpucache\\\":\\\"0\\\",\\\"debuggable\\\":\\\"0\\\",\\\"devicename\\\":\\\"iPhone\\\",\\\"elapsedtime\\\":\\\"0\\\",\\\"fingerprintauth\\\":\\\"0\\\",\\\"hookframe\\\":\\\"58189\\\",\\\"idfv\\\":\\\"7EE78C09-79B5-43FB-B41A-89713E159930\\\",\\\"is_batch\\\":\\\"1\\\",\\\"is_debugged\\\":\\\"0\\\",\\\"is_fake\\\":\\\"0\\\",\\\"is_highrisk\\\":\\\"0\\\",\\\"is_hook\\\":\\\"0\\\",\\\"is_new\\\":\\\"0\\\",\\\"is_proxy\\\":\\\"0\\\",\\\"is_replay\\\":\\\"0\\\",\\\"is_root\\\":\\\"0\\\",\\\"is_rubbish\\\":\\\"0\\\",\\\"is_simulator\\\":\\\"0\\\",\\\"is_trust\\\":\\\"0\\\",\\\"is_unicorn\\\":\\\"0\\\",\\\"is_virtual\\\":\\\"0\\\",\\\"is_wiped\\\":\\\"0\\\",\\\"isfront\\\":\\\"1\\\",\\\"magisk\\\":\\\"0\\\",\\\"model\\\":\\\"iPhone14,2\\\",\\\"networkoperator\\\":\\\"46011,46011\\\",\\\"networktype\\\":\\\"4\\\",\\\"networktype_decode\\\":\\\"4\\\",\\\"new\\\":\\\"0\\\",\\\"packagename\\\":\\\"com.nike.omega.cn\\\",\\\"packagesign\\\":\\\"659784f9d65e15eeccaf859aa859c071\\\",\\\"passwordunlock\\\":\\\"0\\\",\\\"pkgreleasemode\\\":\\\"0\\\",\\\"pkguncrypted\\\":\\\"1\\\",\\\"platform\\\":\\\"1\\\",\\\"proxyinfo\\\":\\\"|\\\",\\\"reason\\\":\\\"000015d8160483a593112edf\\\",\\\"releaseversion\\\":\\\"16.2\\\",\\\"reserve1\\\":\\\"0\\\",\\\"root\\\":\\\"0\\\",\\\"running_frame_cydia\\\":\\\"0\\\",\\\"running_frame_edxposed\\\":\\\"0\\\",\\\"running_frame_fishhook\\\":\\\"0\\\",\\\"running_frame_frida\\\":\\\"0\\\",\\\"running_frame_magisk\\\":\\\"0\\\",\\\"running_frame_script\\\":\\\"0\\\",\\\"running_frame_va\\\":\\\"0\\\",\\\"running_frame_xposed\\\":\\\"0\\\",\\\"screenon\\\":\\\"0\\\",\\\"screenres\\\":\\\"390x844\\\",\\\"secure\\\":\\\"0\\\",\\\"short_uptime\\\":\\\"0\\\",\\\"sign_hmac\\\":\\\"F1171639C7B15BAD1D1EDFC5B3ACD14EF32AF335\\\",\\\"sign_time\\\":\\\"8A9310E43E\\\",\\\"simoperator\\\":\\\"中国电信\\\",\\\"stgproxy\\\":\\\"0\\\",\\\"stgsimulator\\\":\\\"0\\\",\\\"stgydnslocal\\\":\\\"0\\\",\\\"stgydnsnet\\\":\\\"0\\\",\\\"stgyinss\\\":\\\"0\\\",\\\"stgyroot\\\":\\\"0\\\",\\\"stgysimulator\\\":\\\"0\\\",\\\"stgyspitep\\\":\\\"0\\\",\\\"testframe\\\":\\\"0\\\",\\\"timestamp\\\":\\\"1694684466803\\\",\\\"umid\\\":\\\"FC4DADB0-403B-4104-B110-11657589417\\\",\\\"unicorn\\\":\\\"0\\\",\\\"unknown_bssid\\\":\\\"0\\\",\\\"unknown_wifimac\\\":\\\"0\\\",\\\"usbstate\\\":\\\"0\\\",\\\"version\\\":\\\"3.0.4\\\",\\\"virtual\\\":\\\"0\\\"}\"\n" +
                "}";
        wafLogResolver.flatMap(wafLog, collector);
        Assert.assertEquals("FC4DADB0-403B-4104-B110-11657589417", out.get(0).getUser());
        Assert.assertEquals(WafUserType.umid, out.get(0).getUserType());
    }

    @Test
    public void doesNotFail_whenEmptyWafLog() {
        WafLogResolver wafLogResolver = new WafLogResolver();
        List<WafRequestEvent> out = new ArrayList<>();
        ListCollector<WafRequestEvent> collector = new ListCollector<>(out);

        wafLogResolver.flatMap("", collector);
        Assert.assertEquals(0, out.size());
    }



}
