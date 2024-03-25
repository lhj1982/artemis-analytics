package com.nike.artemis.model.rules;


import com.fasterxml.jackson.databind.JsonNode;
import com.nike.artemis.common.CommonConstants;
import com.nike.artemis.model.launch.BlockKind;
import com.nike.artemis.LaunchRateRuleBuilder;
import com.nike.artemis.model.launch.LaunchRequestEvent;

import java.util.HashMap;
import java.util.Map;

public class LaunchRateRule {
    private String ruleId;
    private BlockKind blockKind;
    private String county;
    private String trueClientIp;
    private Long limit;
    private Long windowSize;
    private Long startTime;
    private Long expiration;
    private String action;
    private RuleState ruleState;
    private Map<String, Map<String, Long>> whitelist;
    private Map<String, Map<String, Long>> blacklist;

    public static LaunchRateRule fromRawLine(JsonNode rule) {

        LaunchRateRuleBuilder builder = new LaunchRateRuleBuilder();

        builder.ruleId(rule.get("rule_id").asText());

        if (rule.get("block_kind").asText().compareToIgnoreCase(CommonConstants.LAUNCH_DATA_ADDRESS_COUNTY) == 0) {
            builder.blockKind(BlockKind.county);
            // init white list
            JsonNode whitelist = rule.get("whitelist");
            if (whitelist != null) {
                if (whitelist.isArray()) {
                    Map<String, Map<String, Long>> countyWhitelist = getStringMap(whitelist);
                    builder.whitelist(countyWhitelist);
                }
            }
            // init black list
            JsonNode blacklist = rule.get("blacklist");
            if (blacklist != null) {
                if (blacklist.isArray()) {
                    Map<String, Map<String, Long>> countyBlacklist = getStringMap(blacklist);
                    builder.blacklist(countyBlacklist);
                }
            }
        } else if (rule.get("block_kind").asText().compareToIgnoreCase(CommonConstants.LAUNCH_DATA_IP) == 0) {
            builder.blockKind(BlockKind.ipaddress);
        } else {
            return null;
        }

        builder.limit(Long.valueOf(rule.get("limit").asText()))
                .windowSize(Long.valueOf(rule.get("window_size").asText()))
                .expiration(Long.valueOf(rule.get("block_time").asText()));

        if (rule.get("rule_state").asText().compareToIgnoreCase("ON") == 0) {
            builder.ruleState(RuleState.ON);
        } else if (rule.get("rule_state").asText().compareToIgnoreCase("OFF") == 0) {
            builder.ruleState(RuleState.OFF);
        } else {
            return null;
        }
        builder.action(rule.get("action").asText());
        return builder.build();
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public BlockKind getBlockKind() {
        return blockKind;
    }

    public void setBlockKind(BlockKind blockKind) {
        this.blockKind = blockKind;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getTrueClientIp() {
        return trueClientIp;
    }

    public void setTrueClientIp(String trueClientIp) {
        this.trueClientIp = trueClientIp;
    }

    public Long getLimit() {
        return limit;
    }

    public void setLimit(Long limit) {
        this.limit = limit;
    }

    public Long getWindowSize() {
        return windowSize;
    }

    public void setWindowSize(Long windowSize) {
        this.windowSize = windowSize;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getExpiration() {
        return expiration;
    }

    public void setExpiration(Long expiration) {
        this.expiration = expiration;
    }

    public RuleState getRuleState() {
        return ruleState;
    }

    public void setRuleState(RuleState ruleState) {
        this.ruleState = ruleState;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Map<String, Map<String, Long>> getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(Map<String, Map<String, Long>> whitelist) {
        this.whitelist = whitelist;
    }

    public Map<String, Map<String, Long>> getBlacklist() {
        return blacklist;
    }

    public void setBlacklist(Map<String, Map<String, Long>> blacklist) {
        this.blacklist = blacklist;
    }

    public Boolean appliesTo(LaunchRequestEvent requestEvent) {
        if (this.blockKind == BlockKind.county && requestEvent.getAddresses().get(0).getCounty() != null) {
            return true;
        } else return this.blockKind == BlockKind.ipaddress && requestEvent.getDevice().getTrueClientIp() != null;
    }

    public boolean isEnforce() {
        return ruleState == RuleState.ON;
    }


    public enum RuleState {
        ON,
        OFF
    }

    public LaunchRateRule(String ruleId, BlockKind blockKind, String county, String trueClientIp, Long limit, Long windowSize,
                          Long startTime, Long expiration, RuleState ruleState, String action,
                          Map<String, Map<String, Long>> whitelist, Map<String, Map<String, Long>> blacklist) {
        this.ruleId = ruleId;
        this.blockKind = blockKind;
        this.county = county;
        this.trueClientIp = trueClientIp;
        this.limit = limit;
        this.windowSize = windowSize * 1000L * 60L;
        this.startTime = startTime;
        this.expiration = expiration * 1000L * 60L; // in minutes
        this.ruleState = ruleState;
        this.action = action;
        this.whitelist = whitelist;
        this.blacklist = blacklist;
    }

    public LaunchRateRule(LaunchRateRuleBuilder builder) {
        this(builder.ruleId, builder.blockKind, builder.county, builder.trueClientIp, builder.limit, builder.windowSize,
                builder.startTime, builder.expiration, builder.ruleState, builder.action, builder.whitelist, builder.blacklist);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LaunchRateRule rateRule = (LaunchRateRule) o;
        if (ruleId != null ? !ruleId.equals(rateRule.ruleId) : rateRule.ruleId != null) return false;
        if (blockKind != rateRule.blockKind) return false;
        if (county != null ? !county.equals(rateRule.county) : rateRule.county != null) return false;
        if (trueClientIp != null ? !trueClientIp.equals(rateRule.trueClientIp) : rateRule.trueClientIp != null)
            return false;
        if (limit != null ? !limit.equals(rateRule.limit) : rateRule.limit != null) return false;
        if (windowSize != null ? !windowSize.equals(rateRule.windowSize) : rateRule.windowSize != null) return false;
        if (expiration != null ? !expiration.equals(rateRule.expiration) : rateRule.expiration != null) return false;
        if (action != null ? !action.equals(rateRule.action) : rateRule.action != null) return false;
        return ruleState == rateRule.ruleState;
    }

    @Override
    public int hashCode() {
        int result = 7;
        result = 37 * result + (ruleId != null ? ruleId.hashCode() : 0);
        result = 37 * result + (blockKind != null ? blockKind.asString().hashCode() : 0);
        result = 37 * result + (county != null ? county.hashCode() : 0);
        result = 37 * result + (trueClientIp != null ? trueClientIp.hashCode() : 0);
        result = 37 * result + Math.toIntExact(limit);
        result = 37 * result + (int) (windowSize ^ (windowSize >>> 32));
        result = 37 * result + (int) (expiration ^ (expiration >>> 32));
        result = 37 * result + (ruleState == RuleState.ON ? 1 : 0);
        result = 37 * result + (action != null ? action.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("com.nike.artemis.RateRule.%s.%s.%dc.%dw.%dbs.%dbe.%s.%s",
                blockKind.name(),
                String.join("-", county, trueClientIp),
                limit,
                windowSize,
                startTime,
                expiration,
                ruleState.name(),
                action
        );
    }

    private static Map<String, Map<String, Long>> getStringMap(JsonNode list) {
        Map<String, Map<String, Long>> countyList = new HashMap<>();
        for (JsonNode obj : list) {
            String city = obj.get(CommonConstants.LAUNCH_DATA_ADDRESS_CITY).asText();
            JsonNode county = obj.get(CommonConstants.LAUNCH_DATA_ADDRESS_COUNTY);
            Map<String, Long> countyMap = new HashMap<>();
            if (county.isArray()) {
                county.forEach(v -> countyMap.put(v.asText(), 0L));
            } else if (county.isObject()) {
                county.fieldNames().forEachRemaining(k -> countyMap.put(k, county.get(k).asLong()));
            }
            countyList.put(city, countyMap);
        }
        return countyList;
    }
}
