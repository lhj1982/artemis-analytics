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
    public void test_identifyWafUser_upmid() throws Exception {
        Tuple2<WafUserType, Tuple2<String, String>> result = Tuple2.of(WafUserType.upmid, Tuple2.of("bacd8444-6a51-4ea1-9585-691a33173e5d", AccountType.PLUS.getType()));
        WafData wafData = new WafData();
        wafData.setRequest_path("/token/v1");
        wafData.setReal_client_ip("182.150.27.228");
        wafData.setRequest_body("client_id=e7cc33c015ad3360f16b967b692ea595&refresh_token=eyJhbGciOiJSUzI1NiIsImtpZCI6IjgyNzIxYjVkLTQwMTAtNDVkOC04ZmYxLTNjZDNhZTY0YTMwMXNpZyJ9.eyJpYXQiOjE3MTMzNzA1MDcsImV4cCI6MTc0NDkwNjUwNywiaXNzIjoib2F1dGgyaWR0LWNuIiwianRpIjoiZDQ5YzMwNDItZDZjNi00Yjc2LWE1ZjMtMDFmZDIzODZkMTAzIiwiYXVkIjoib2F1dGgyaWR0IiwidHJ1c3QiOjEwMCwibGF0IjoxNzEzMjg0NDg0LCJjbGkiOiJlN2NjMzNjMDE1YWQzMzYwZjE2Yjk2N2I2OTJlYTU5NSIsInN1YiI6ImJhY2Q4NDQ0LTZhNTEtNGVhMS05NTg1LTY5MWEzMzE3M2U1ZCIsInNidCI6Im5pa2U6cGx1cyIsInNjcCI6WyJuaWtlLmRpZ2l0YWwiXSwicnNpZCI6IjM4OGRlMDkyLThjZDUtNDRjOS1iYzM2LTc2NDJmMjgzZGU3ZCIsImxyc2NwIjoib3BlbmlkIHByb2ZpbGUgZW1haWwgcGhvbmUgZmxvdyBvZmZsaW5lX2FjY2VzcyBuaWtlLmRpZ2l0YWwifQ.luIElAAMtuX9enTOMurp33rfhj0zfncAel8XARE_NcvEsGryr6_qxTKDhfHdwO7TLTK1YiYHYNVSomBiDWQ4t-XDXhQ7eYft-GscVHGl3357-O4E9t10MbAY9XA5bNjnbGi4MY7HAsw51vpa1ttaOkagUw4Ei2QQne1pgKXdl8lt9xHEnM6voq6V-lSr2eJWC7tyvbVKvDiaWVNXDhpW-SuHCt45VN6YD-VOXs6Kly8imHCS8fAiqAVlpUxTehk3XahvlGTNsS1-nvAZhNz_yaTrxZbwX0Al9swtyCHgbMRh-Dlz4MOi0WRxu7_l3o30r-pPwXMxw46kLwxBJMgOww&grant_type=refresh_token");
        Tuple2<WafUserType, Tuple2<String, String>> actual = UserIdentifier.identifyWafUser(PATHS, wafData);
        assertEquals(result, actual);
    }

    @Test
    public void test_identifyWafUser_umid() throws Exception {
        Tuple2<WafUserType, Tuple2<String, String>> result = Tuple2.of(WafUserType.umid, Tuple2.of("umid123", AccountType.PLUS.getType()));
        WafData wafData = new WafData();
        wafData.setRequest_path("/orders/history");
        wafData.setReal_client_ip("182.150.27.228");
        wafData.setWxbb_info_tbl("{\"new\":\"\",\"reason\":\"wToken header not found\",\"umid\":\"umid123\"}");
        Tuple2<WafUserType, Tuple2<String, String>> actual = UserIdentifier.identifyWafUser(PATHS, wafData);
        assertEquals(result, actual);
    }

    @Test
    public void test_identifyWafUser_phonenumber1() throws Exception {
        Tuple2<WafUserType, Tuple2<String, String>> result = Tuple2.of(WafUserType.phonenumber, Tuple2.of("+8613356789876", AccountType.PLUS.getType()));
        WafData wafData = new WafData();
        wafData.setRequest_path("/credential_lookup/v1");
        wafData.setReal_client_ip("182.150.27.228");
        wafData.setRequest_body("{\"credential\":\"+8613356789876\",\"client_id\":\"e586fabad2bfc03bf416f6a5419837a4\"}");
        Tuple2<WafUserType, Tuple2<String, String>> actual = UserIdentifier.identifyWafUser(PATHS, wafData);
        assertEquals(result, actual);
    }

    @Test
    public void test_identifyWafUser_phonenumber2() throws Exception {
        Tuple2<WafUserType, Tuple2<String, String>> result = Tuple2.of(WafUserType.phonenumber, Tuple2.of("+8613356789876", AccountType.PLUS.getType()));
        WafData wafData = new WafData();
        wafData.setRequest_path("/challenge/password/v1");
        wafData.setReal_client_ip("182.150.27.228");
        wafData.setRequest_body("{\"destination\":\"+8613356789876\",\"country\":\"CN\",\"type\":\"CHALLENGE\",\"client_id\":\"e586fabad2bfc03bf416f6a5419837a4\",\"language\":\"zh-Hans\",\"swoosh_login\":false}");
        Tuple2<WafUserType, Tuple2<String, String>> actual = UserIdentifier.identifyWafUser(PATHS, wafData);
        assertEquals(result, actual);
    }

    @Test
    public void test_identifyWafUser_ipaddress1() throws Exception {
        Tuple2<WafUserType, Tuple2<String, String>> result = Tuple2.of(WafUserType.ipaddress, Tuple2.of("182.150.27.228", AccountType.PLUS.getType()));
        WafData wafData = new WafData();
        wafData.setRequest_path("/orders/history");
        wafData.setReal_client_ip("182.150.27.228");
        Tuple2<WafUserType, Tuple2<String, String>> actual = UserIdentifier.identifyWafUser(PATHS, wafData);
        assertEquals(result, actual);
    }

    @Test
    public void test_identifyWafUser_ipaddress2() throws Exception {
        Tuple2<WafUserType, Tuple2<String, String>> result = Tuple2.of(WafUserType.ipaddress, Tuple2.of("182.150.27.228", AccountType.PLUS.getType()));
        WafData wafData = new WafData();
        wafData.setRequest_path("/challenge/password/v1");
        wafData.setReal_client_ip("182.150.27.228");
        Tuple2<WafUserType, Tuple2<String, String>> actual = UserIdentifier.identifyWafUser(PATHS, wafData);
        assertEquals(result, actual);
    }
}
