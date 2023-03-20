package com.nike.artemis;

public class RateRuleBuilder {

    public BlockKind blockKind;
    public String county;
    public String trueClientIp;
    public String upmid;
    public Long limit;
    public Long windowSize;
    public Long startTime;
    public Long expiration;
    public RateRule.RuleState ruleState;

    public RateRuleBuilder(){}

    public RateRuleBuilder blockKind(BlockKind blockKind){
        this.blockKind = blockKind;
        return this;
    }

    public RateRuleBuilder county(String county){
        this.county = county;
        return this;
    }

    public RateRuleBuilder trueClientIp(String trueClientIp){
        this.trueClientIp = trueClientIp;
        return this;
    }

    public RateRuleBuilder upmid(String upmid){
        this.upmid = upmid;
        return this;
    }

    public RateRuleBuilder limit(Long limit){
        this.limit = limit;
        return this;
    }

    public RateRuleBuilder windowSize(Long windowSize){
        this.windowSize = windowSize;
        return this;
    }

    public RateRuleBuilder startTime(Long startTime){
        this.startTime = startTime;
        return this;
    }

    public RateRuleBuilder expiration(Long expiration){
        this.expiration = expiration;
        return this;
    }

    public RateRuleBuilder ruleState(RateRule.RuleState ruleState){
        this.ruleState = ruleState;
        return this;
    }

    public RateRule build(){
        return new RateRule(this);
    }


}
