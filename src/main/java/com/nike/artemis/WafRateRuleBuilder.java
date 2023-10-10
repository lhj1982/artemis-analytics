package com.nike.artemis;

import com.nike.artemis.model.EnforceType;
import com.nike.artemis.model.rules.WafRateRule;

public class WafRateRuleBuilder {

    public String rule_id;
    public String rule_name;
    public String user_type;
    public String path;
    public String method;
    public String status;
    public long window;
    public long limit;
    public long block_time;
    public EnforceType enforce;
    public String name_space;
    public String action;
    public long ttl;

    public WafRateRuleBuilder() {
    }

    public WafRateRuleBuilder rule_id (String rule_id) {
        this.rule_id = rule_id;
        return this;
    }
    public WafRateRuleBuilder rule_name(String rule_name) {
        this.rule_name = rule_name;
        return this;
    }

    public WafRateRuleBuilder user_type(String user_type) {
        this.user_type = user_type;
        return this;
    }

    public WafRateRuleBuilder path(String path) {
        this.path = path;
        return this;
    }

    public WafRateRuleBuilder method(String method) {
        this.method = method;
        return this;
    }

    public WafRateRuleBuilder status(String status) {
        this.status = status;
        return this;
    }

    public WafRateRuleBuilder window(Long window) {
        this.window = window;
        return this;
    }

    public WafRateRuleBuilder limit(Long limit) {
        this.limit = limit;
        return this;
    }

    public WafRateRuleBuilder block_time(Long block_time) {
        this.block_time = block_time;
        return this;
    }

    public WafRateRuleBuilder enforce(EnforceType enforce) {
        this.enforce = enforce;
        return this;
    }

    public WafRateRuleBuilder name_space(String name_space) {
        this.name_space = name_space;
        return this;
    }

    public WafRateRuleBuilder action(String action) {
        this.action = action;
        return this;
    }
    public WafRateRuleBuilder ttl(long ttl) {
        this.ttl = ttl;
        return this;
    }
    public WafRateRule build() {
        return new WafRateRule(this);
    }


}
