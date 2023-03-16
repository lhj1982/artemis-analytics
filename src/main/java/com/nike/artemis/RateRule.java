package com.nike.artemis;

import org.apache.flink.api.java.tuple.Tuple2;

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

    public Tuple2<BlockKind, String> appliesTo(RequestEvent requestEvent) {
        if (this.blockKind == BlockKind.county && requestEvent.getAddresses().get(0).getCounty().equals(this.county)) {
                return new Tuple2<>(BlockKind.county, this.county);
        } else {
            return new Tuple2<>(null, null);
        }
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
        if (startTime != null ? !startTime.equals(rateRule.startTime) : rateRule.startTime != null) return false;
        if (expiration != null ? !expiration.equals(rateRule.expiration) : rateRule.expiration != null) return false;
        return ruleState == rateRule.ruleState;
    }

    @Override
    public int hashCode() {
        int result = blockKind != null ? blockKind.hashCode() : 0;
        result = 31 * result + (county != null ? county.hashCode() : 0);
        result = 31 * result + (trueClientIp != null ? trueClientIp.hashCode() : 0);
        result = 31 * result + (upmid != null ? upmid.hashCode() : 0);
        result = 31 * result + (limit != null ? limit.hashCode() : 0);
        result = 31 * result + (windowSize != null ? windowSize.hashCode() : 0);
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        result = 31 * result + (expiration != null ? expiration.hashCode() : 0);
        result = 31 * result + (ruleState != null ? ruleState.hashCode() : 0);
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
