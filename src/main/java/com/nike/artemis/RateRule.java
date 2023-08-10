package com.nike.artemis;


import com.fasterxml.jackson.databind.JsonNode;

public class RateRule {
    private BlockKind blockKind;
    private String county;
    private String trueClientIp;
    private String upmid;
    private Long limit;
    private Long windowSize;
    private Long startTime;
    private Long expiration;
    private RuleState ruleState;

    public static RateRule fromRawLine(JsonNode rule) {

        RateRuleBuilder builder = new RateRuleBuilder();

        if (rule.get("block_kind").asText().compareToIgnoreCase("county") == 0){
            builder.blockKind(BlockKind.county);
        } else if (rule.get("block_kind").asText().compareToIgnoreCase("trueClientIp") == 0) {
            builder.blockKind(BlockKind.trueClientIp);
        } else if (rule.get("block_kind").asText().compareToIgnoreCase("upmid") == 0) {
            builder.blockKind(BlockKind.upmid);
        } else {
            return null;
        }

        builder.limit(Long.valueOf(rule.get("limit").asText()))
                .windowSize(Long.valueOf(rule.get("window_size").asText()))
                .expiration(Long.valueOf(rule.get("block_time").asText()));

        if (rule.get("rule_state").asText().compareToIgnoreCase("ON") == 0){
            builder.ruleState(RuleState.ON);
        } else if (rule.get("rule_state").asText().compareToIgnoreCase("OFF") == 0) {
            builder.ruleState(RuleState.OFF);
        } else {
            return null;
        }
        return builder.build();
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

    public String getUpmid() {
        return upmid;
    }

    public void setUpmid(String upmid) {
        this.upmid = upmid;
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

    public Boolean appliesTo(RequestEvent requestEvent) {
        if (this.blockKind == BlockKind.county && requestEvent.getAddresses().get(0).getCounty() != null) {
            return true;
        } else if (this.blockKind == BlockKind.upmid && requestEvent.getUser().getUpmId() != null) {
            return true;
        } else return this.blockKind == BlockKind.trueClientIp && requestEvent.getDevice().getTrueClientIp() != null;
    }

    public boolean isEnforce() {
        return ruleState == RuleState.ON;
    }


    enum RuleState {
        ON,
        OFF
    }

    public RateRule(BlockKind blockKind, String county, String trueClientIp, String upmid,  Long limit, Long windowSize, Long startTime, Long expiration, RuleState ruleState) {
        this.blockKind = blockKind;
        this.county = county;
        this.trueClientIp = trueClientIp;
        this.upmid = upmid;
        this.limit = limit;
        this.windowSize = windowSize * 1000L * 60L;
        this.startTime = startTime;
        this.expiration = expiration * 1000L * 60L; // in minutes
        this.ruleState = ruleState;
    }

    public RateRule(RateRuleBuilder builder){
        this(builder.blockKind, builder.county, builder.trueClientIp, builder.upmid, builder.limit, builder.windowSize, builder.startTime, builder.expiration, builder.ruleState);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RateRule rateRule = (RateRule) o;

        if (blockKind != rateRule.blockKind) return false;
        if (county != null ? !county.equals(rateRule.county) : rateRule.county != null) return false;
        if (trueClientIp != null ? !trueClientIp.equals(rateRule.trueClientIp) : rateRule.trueClientIp != null)
            return false;
        if (upmid != null ? !upmid.equals(rateRule.upmid) : rateRule.upmid != null) return false;
        if (limit != null ? !limit.equals(rateRule.limit) : rateRule.limit != null) return false;
        if (windowSize != null ? !windowSize.equals(rateRule.windowSize) : rateRule.windowSize != null) return false;
        if (expiration != null ? !expiration.equals(rateRule.expiration) : rateRule.expiration != null) return false;
        return ruleState == rateRule.ruleState;
    }

    @Override
    public int hashCode() {
        int result = 7;
        result = 37 * result + (blockKind != null ? blockKind.asInt() : 0);
        result = 37 * result + (county != null ? county.hashCode() : 0);
        result = 37 * result + (trueClientIp != null ? trueClientIp.hashCode() : 0);
        result = 37 * result + (upmid != null ? upmid.hashCode() : 0);
        result = 37 * result + Math.toIntExact(limit);
        result = 37 * result + (int)(windowSize ^ (windowSize >>> 32));
        result = 37 * result + (int)(expiration ^ (expiration >>> 32));
        result = 37 * result + (ruleState == RuleState.ON ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("com.nike.artemis.RateRule.%s.%s.%dc.%dw.%dbs.%dbe.%s",
                blockKind.name(),
                String.join("-", county,trueClientIp,upmid),
                limit,
                windowSize,
                startTime,
                expiration,
                ruleState.name()
                );
    }
}
