package com.nike.artemis;

import com.nike.artemis.model.EnforceType;
import com.nike.artemis.model.rules.CdnRateRule;

public class CdnRateRuleBuilder {

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

    public CdnRateRuleBuilder() {
    }

    public CdnRateRuleBuilder rule_id(String rule_id) {
        this.rule_id = rule_id;
        return this;
    }
    public CdnRateRuleBuilder rule_name(String rule_name) {
        this.rule_name = rule_name;
        return this;
    }

    public CdnRateRuleBuilder user_type(String user_type) {
        this.user_type = user_type;
        return this;
    }

    public CdnRateRuleBuilder path(String path) {
        this.path = path;
        return this;
    }

    public CdnRateRuleBuilder method(String method) {
        this.method = method;
        return this;
    }

    public CdnRateRuleBuilder status(String status) {
        this.status = status;
        return this;
    }

    public CdnRateRuleBuilder window(Long window) {
        this.window = window;
        return this;
    }

    public CdnRateRuleBuilder limit(Long limit) {
        this.limit = limit;
        return this;
    }

    public CdnRateRuleBuilder block_time(Long block_time) {
        this.block_time = block_time;
        return this;
    }

    public CdnRateRuleBuilder enforce(EnforceType enforce) {
        this.enforce = enforce;
        return this;
    }

    public CdnRateRuleBuilder name_space(String name_space) {
        this.name_space = name_space;
        return this;
    }

    public CdnRateRuleBuilder action(String action) {
        this.action = action;
        return this;
    }
    public CdnRateRuleBuilder ttl(long ttl) {
        this.ttl = ttl;
        return this;
    }
    public CdnRateRule build() {
        return new CdnRateRule(this);
    }


}
