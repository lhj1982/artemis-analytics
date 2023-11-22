package com.nike.artemis.Utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nike.artemis.model.cdn.CdnData;
import com.nike.artemis.model.cdn.CdnUserType;
import com.nike.artemis.model.jwt.JwtPayload;
import com.nike.artemis.model.waf.WafData;
import com.nike.artemis.model.waf.WafUserType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.java.tuple.Tuple2;

import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class UserIdentifier {
    static Pattern JWT_REGEX = Pattern.compile("([A-Za-z0-9+/=]+\\.[A-Za-z0-9+/=]+\\.[^.\\s]+)");
    static Base64.Decoder DECODER = Base64.getUrlDecoder();
    static ObjectMapper mapper = new ObjectMapper();
    public static Tuple2<CdnUserType, String> identifyCdnUser(CdnData cdnData) throws JsonProcessingException {
        if (cdnData.getUser_info().contains("auth=Bearer ")) {
            String jwtToken = cdnData.getUser_info().split("auth=Bearer ")[1].split("\\|\\|cacheCtl")[0];
            if (JWT_REGEX.matcher(jwtToken).find()) {
                String upmid = getUpmid(jwtToken);
                if (upmid != null) return Tuple2.of(CdnUserType.upmid, upmid);
            }
        }
        return Tuple2.of(CdnUserType.ipaddress, cdnData.getClient_ip());
    }

    private static String getUpmid(String jwtToken) throws JsonProcessingException {
        String rawPayload = jwtToken.split("\\.")[1];
        String payload = new String(DECODER.decode(rawPayload));
        JwtPayload jwtPayload = mapper.readValue(payload, JwtPayload.class);
        return jwtPayload.getPrn();
    }

    public static Tuple2<WafUserType, String> identifyWafUser(List<String> paths, WafData wafData) throws JsonProcessingException {
        if (wafData.getWxbb_info_tbl() != null) {
            String umid = mapper.readTree(wafData.getWxbb_info_tbl()).get("umid").textValue();
            if (!umid.equals("")) {
                return Tuple2.of(WafUserType.umid, mapper.readTree(wafData.getWxbb_info_tbl()).get("umid").textValue());
            }
        }
        if (checkPath(paths, wafData.getRequest_path())) {
            if (StringUtils.isNoneBlank(wafData.getRequest_body())) {
                JsonNode requestBody;
                // deal with situations where conversion to json may not be possible, eg: "-"
                try {
                    requestBody = mapper.readTree(wafData.getRequest_body());
                } catch (JsonProcessingException e) {
                    return Tuple2.of(WafUserType.ipaddress, wafData.getReal_client_ip());
                }
                // get credential or destination as phone number
                JsonNode phoneNumber = requestBody.get("credential");
                if (Objects.isNull(phoneNumber)) {
                    phoneNumber = requestBody.get("destination");
                    if (Objects.isNull(phoneNumber)) {
                        return Tuple2.of(WafUserType.ipaddress, wafData.getReal_client_ip());
                    }
                }
                if (StringUtils.isNoneBlank(phoneNumber.textValue())) {
                    return Tuple2.of(WafUserType.phonenumber, phoneNumber.textValue());
                }
            }
        }
        return Tuple2.of(WafUserType.ipaddress, wafData.getReal_client_ip());
    }

    private static Boolean checkPath(List<String> paths, String path) {
        if (CollectionUtils.isEmpty(paths)) {
            return false;
        }
        for (String p : paths) {
            if (path.startsWith(p)) {
                return true;
            }
        }
        return false;
    }
}
