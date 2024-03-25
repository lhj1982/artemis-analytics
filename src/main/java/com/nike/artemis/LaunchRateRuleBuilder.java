package com.nike.artemis;

import com.nike.artemis.model.launch.BlockKind;
import com.nike.artemis.model.rules.LaunchRateRule;

import java.util.Map;

public class LaunchRateRuleBuilder {

    public String ruleId;
    public BlockKind blockKind;
    public String county;
    public String trueClientIp;
    public Long limit;
    public Long windowSize;
    public Long startTime;
    public Long expiration;
    public String action;
    public Map<String, Map<String, Long>> whitelist;
    public Map<String, Map<String, Long>> blacklist;
    public LaunchRateRule.RuleState ruleState;

    public LaunchRateRuleBuilder(){}

    public LaunchRateRuleBuilder ruleId(String ruleId) {
        this.ruleId = ruleId;
        return this;
    }
    public LaunchRateRuleBuilder blockKind(BlockKind blockKind){
        this.blockKind = blockKind;
        return this;
    }

    public LaunchRateRuleBuilder county(String county){
        this.county = county;
        return this;
    }

    public LaunchRateRuleBuilder trueClientIp(String trueClientIp){
        this.trueClientIp = trueClientIp;
        return this;
    }

    public LaunchRateRuleBuilder limit(Long limit){
        this.limit = limit;
        return this;
    }

    public LaunchRateRuleBuilder windowSize(Long windowSize){
        this.windowSize = windowSize;
        return this;
    }

    public LaunchRateRuleBuilder startTime(Long startTime){
        this.startTime = startTime;
        return this;
    }

    public LaunchRateRuleBuilder expiration(Long expiration){
        this.expiration = expiration;
        return this;
    }

    public LaunchRateRuleBuilder ruleState(LaunchRateRule.RuleState ruleState){
        this.ruleState = ruleState;
        return this;
    }
    public LaunchRateRuleBuilder action(String action){
        this.action = action;
        return this;
    }
    public LaunchRateRuleBuilder whitelist(Map<String, Map<String, Long>> whitelist){
        this.whitelist = whitelist;
        return this;
    }
    public LaunchRateRuleBuilder blacklist(Map<String, Map<String, Long>> blacklist){
        this.blacklist = blacklist;
        return this;
    }
    public LaunchRateRule build(){
        return new LaunchRateRule(this);
    }


}
