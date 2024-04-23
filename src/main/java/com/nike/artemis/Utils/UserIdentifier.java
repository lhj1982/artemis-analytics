package com.nike.artemis.Utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nike.artemis.model.AccountType;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserIdentifier {
    private static final String SOURCE_TYPE_CDN = "cdn";
    private static final String SOURCE_TYPE_WAF = "waf";
    static Pattern JWT_REGEX = Pattern.compile("([A-Za-z0-9+/=]+\\.[A-Za-z0-9+/=]+\\.[^.\\s]+)");
    static Base64.Decoder DECODER = Base64.getUrlDecoder();
    static ObjectMapper mapper = new ObjectMapper();

    /**
     * Identifies the type of CDN user based on the provided incoming {@code CdnData}.
     *
     * @param cdnData the CDN request data containing user information.
     * @return a tuple containing the user type and a tuple containing upmid/ipaddress and account type.
     * @throws JsonProcessingException if there is an error processing the JSON data.
     */
    public static Tuple2<CdnUserType, Tuple2<String, String>> identifyCdnUser(CdnData cdnData) throws JsonProcessingException {
        if (cdnData.getUser_info() != null && cdnData.getUser_info().contains("auth=Bearer ")) {
            String jwtToken = cdnData.getUser_info().split("auth=Bearer ")[1].split("\\|\\|cacheCtl")[0];
            if (JWT_REGEX.matcher(jwtToken).find()) {
                Tuple2<String, String> upmidAndAccountType = getUpmidAndAccountType(jwtToken, SOURCE_TYPE_CDN);
                if (upmidAndAccountType.f0 != null) return Tuple2.of(CdnUserType.upmid, upmidAndAccountType);
            }
        }
        return Tuple2.of(CdnUserType.ipaddress, Tuple2.of(cdnData.getClient_ip(), AccountType.PLUS.getType()));
    }

    /**
     * Extract the Upmid and AccountType from the provided JWT String.
     *
     * @param jwtToken the jwt string.
     * @return A tuple of containing Upmid and AccountType.
     * @throws JsonProcessingException if there is an error processing the JSON data.
     */
    private static Tuple2<String, String> getUpmidAndAccountType(String jwtToken, String sourceTpye) throws JsonProcessingException {
        String rawPayload = jwtToken.split("\\.")[1];
        String payload = new String(DECODER.decode(rawPayload));
        JwtPayload jwtPayload = mapper.readValue(payload, JwtPayload.class);
        if (SOURCE_TYPE_CDN.equals(sourceTpye)) {
            return Tuple2.of(jwtPayload.getPrn(), jwtPayload.getPrt());
        } else if (SOURCE_TYPE_WAF.equals(sourceTpye)) {
            return Tuple2.of(jwtPayload.getSub(), jwtPayload.getSbt());
        }
        return Tuple2.of(StringUtils.EMPTY, StringUtils.EMPTY);
    }

    public static Tuple2<WafUserType, Tuple2<String, String>> identifyWafUser(List<String> paths, WafData wafData)
            throws JsonProcessingException {
        if (wafData.getRequest_body() != null && wafData.getRequest_body().contains("refresh_token=")) {
            Pattern pattern = Pattern.compile("refresh_token=([^&]+)");
            Matcher matcher = pattern.matcher(wafData.getRequest_body());
            if (matcher.find()) {
                String jwtToken = matcher.group(1);
                Tuple2<String, String> upmidAndAccountType = getUpmidAndAccountType(jwtToken, SOURCE_TYPE_WAF);
                if (upmidAndAccountType.f0 != null) return Tuple2.of(WafUserType.upmid, upmidAndAccountType);
            }
        }
        if (wafData.getWxbb_info_tbl() != null) {
            String umid = mapper.readTree(wafData.getWxbb_info_tbl()).get("umid").textValue();
            if (!umid.isEmpty()) {
                return Tuple2.of(WafUserType.umid, Tuple2.of(umid, AccountType.PLUS.getType()));
            }
        }
        if (checkPath(paths, wafData.getRequest_path())) {
            if (StringUtils.isNoneBlank(wafData.getRequest_body())) {
                JsonNode requestBody;
                // deal with situations where conversion to json may not be possible, eg: "-"
                try {
                    requestBody = mapper.readTree(wafData.getRequest_body());
                } catch (JsonProcessingException e) {
                    return Tuple2.of(WafUserType.ipaddress, Tuple2.of(wafData.getReal_client_ip(), AccountType.PLUS.getType()));
                }
                // get credential or destination as phone number
                JsonNode phoneNumber = requestBody.get("credential");
                if (Objects.isNull(phoneNumber)) {
                    phoneNumber = requestBody.get("destination");
                    if (Objects.isNull(phoneNumber)) {
                        return Tuple2.of(WafUserType.ipaddress, Tuple2.of(wafData.getReal_client_ip(), AccountType.PLUS.getType()));
                    }
                }
                if (StringUtils.isNoneBlank(phoneNumber.textValue())) {
                    return Tuple2.of(WafUserType.phonenumber, Tuple2.of(phoneNumber.textValue(), AccountType.PLUS.getType()));
                }
            }
        }
        return Tuple2.of(WafUserType.ipaddress, Tuple2.of(wafData.getReal_client_ip(), AccountType.PLUS.getType()));
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
