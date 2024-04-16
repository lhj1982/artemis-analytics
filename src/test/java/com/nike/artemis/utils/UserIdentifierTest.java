package com.nike.artemis.utils;

import com.nike.artemis.Utils.UserIdentifier;
import com.nike.artemis.model.AccountType;
import com.nike.artemis.model.cdn.CdnData;
import com.nike.artemis.model.cdn.CdnUserType;
import com.nike.artemis.model.waf.WafData;
import com.nike.artemis.model.waf.WafUserType;
import org.apache.flink.api.java.tuple.Tuple2;
import org.junit.Test;

import java.util.Base64;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class UserIdentifierTest {

    private static final List<String> PATHS = List.of("/credential_lookup/v1",
            "/challenge/password/v1", "/verification_code/send/v1", "/password_reset/v1");

    @Test
    public void test_identifyCdnUser_upmidWithSwooshAccount() throws Exception {
        Tuple2<CdnUserType, Tuple2<String, String>> result = Tuple2.of(CdnUserType.upmid, Tuple2.of("12345zxcvb", "nike:swoosh"));
        CdnData cdnData = new CdnData();
        String payload = "{\"prn\":\"12345zxcvb\",\"prt\":\"nike:swoosh\"}";
        Base64.Encoder encoder = Base64.getUrlEncoder();
        String jwtToken = "auth=Bearer start." + encoder.encodeToString(payload.getBytes()) + ".end";
        cdnData.setUser_info(jwtToken);
        Tuple2<CdnUserType, Tuple2<String, String>> actual = UserIdentifier.identifyCdnUser(cdnData);
        assertEquals(actual.f1.f1, AccountType.SWOOSH.getType());
        assertEquals(result, actual);
    }

    @Test
    public void test_identifyCdnUser_upmidWithPlusAccount() throws Exception {
        Tuple2<CdnUserType, Tuple2<String, String>> result = Tuple2.of(CdnUserType.upmid, Tuple2.of("12345zxcvb", "nike:plus"));
        CdnData cdnData = new CdnData();
        String payload = "{\"prn\":\"12345zxcvb\",\"prt\":\"nike:plus\"}";
        Base64.Encoder encoder = Base64.getUrlEncoder();
        String jwtToken = "auth=Bearer start." + encoder.encodeToString(payload.getBytes()) + ".end";
        cdnData.setUser_info(jwtToken);
        Tuple2<CdnUserType, Tuple2<String, String>> actual = UserIdentifier.identifyCdnUser(cdnData);
        assertEquals(actual.f1.f1, AccountType.PLUS.getType());
        assertEquals(result, actual);
    }

    @Test
    public void test_returnPlusAccountType_whenCdnLogWithIpAddress() throws Exception {
        Tuple2<CdnUserType, Tuple2<String, String>> result = Tuple2.of(CdnUserType.ipaddress, Tuple2.of("127.0.0.1", "nike:plus"));
        CdnData cdnData = new CdnData();
        cdnData.setClient_ip("127.0.0.1");
        Tuple2<CdnUserType, Tuple2<String, String>> actual = UserIdentifier.identifyCdnUser(cdnData);
        assertEquals(actual.f1.f1, AccountType.PLUS.getType());
        assertEquals(result, actual);
    }

    @Test
    public void test_identifyWafUser_umid() throws Exception {

        Tuple2<WafUserType, String> result = Tuple2.of(WafUserType.umid, "umid123");
        WafData wafData = new WafData();
        wafData.setRequest_path("/orders/history");
        wafData.setReal_client_ip("182.150.27.228");
        wafData.setWxbb_info_tbl("{\"new\":\"\",\"reason\":\"wToken header not found\",\"umid\":\"umid123\"}");
        Tuple2<WafUserType, String> actual = UserIdentifier.identifyWafUser(PATHS, wafData);
        assertEquals(result, actual);
    }

    @Test
    public void test_identifyWafUser_phonenumber1() throws Exception {
        Tuple2<WafUserType, String> result = Tuple2.of(WafUserType.phonenumber, "+8613356789876");
        WafData wafData = new WafData();
        wafData.setRequest_path("/credential_lookup/v1");
        wafData.setReal_client_ip("182.150.27.228");
        wafData.setRequest_body("{\"credential\":\"+8613356789876\",\"client_id\":\"e586fabad2bfc03bf416f6a5419837a4\"}");
        Tuple2<WafUserType, String> actual = UserIdentifier.identifyWafUser(PATHS, wafData);
        assertEquals(result, actual);
    }

    @Test
    public void test_identifyWafUser_phonenumber2() throws Exception {
        Tuple2<WafUserType, String> result = Tuple2.of(WafUserType.phonenumber, "+8613356789876");
        WafData wafData = new WafData();
        wafData.setRequest_path("/challenge/password/v1");
        wafData.setReal_client_ip("182.150.27.228");
        wafData.setRequest_body("{\"destination\":\"+8613356789876\",\"country\":\"CN\",\"type\":\"CHALLENGE\",\"client_id\":\"e586fabad2bfc03bf416f6a5419837a4\",\"language\":\"zh-Hans\",\"swoosh_login\":false}");
        Tuple2<WafUserType, String> actual = UserIdentifier.identifyWafUser(PATHS, wafData);
        assertEquals(result, actual);
    }

    @Test
    public void test_identifyWafUser_ipaddress1() throws Exception {
        Tuple2<WafUserType, String> result = Tuple2.of(WafUserType.ipaddress, "182.150.27.228");
        WafData wafData = new WafData();
        wafData.setRequest_path("/orders/history");
        wafData.setReal_client_ip("182.150.27.228");
        Tuple2<WafUserType, String> actual = UserIdentifier.identifyWafUser(PATHS, wafData);
        assertEquals(result, actual);
    }

    @Test
    public void test_identifyWafUser_ipaddress2() throws Exception {
        Tuple2<WafUserType, String> result = Tuple2.of(WafUserType.ipaddress, "182.150.27.228");
        WafData wafData = new WafData();
        wafData.setRequest_path("/challenge/password/v1");
        wafData.setReal_client_ip("182.150.27.228");
        Tuple2<WafUserType, String> actual = UserIdentifier.identifyWafUser(PATHS, wafData);
        assertEquals(result, actual);
    }
}
