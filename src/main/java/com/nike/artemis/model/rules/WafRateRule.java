package com.nike.artemis.model.rules;

import com.fasterxml.jackson.databind.JsonNode;
import com.nike.artemis.WafRateRuleBuilder;
import com.nike.artemis.model.EnforceType;
import com.nike.artemis.model.waf.WafRequestEvent;

public class WafRateRule {
    private String rule_id;
    private String rule_name;
    private String user_type;
    private String path;
    private String method;
    private String status;
    private long window;
    private long limit;
    private long block_time;
    private EnforceType enforce;
    private String name_space;
    private String action;
    private long ttl;

    public WafRateRule() {
    }

    public WafRateRule(String rule_id, String rule_name, String user_type, String path, String method, String status, long window, long limit, long block_time, EnforceType enforce, String name_space, String action,long ttl) {
        this.rule_id = rule_id;
        this.rule_name = rule_name;
        this.user_type = user_type;
        this.path = path;
        this.method = method;
        this.status = status;
        this.window = window * 1000L * 60L; // in minutes
        this.limit = limit;
        this.block_time = block_time * 1000L * 60L; // in minutes
        this.enforce = enforce;
        this.name_space = name_space;
        this.action = action;
        this.ttl = ttl;
    }

    public String getRule_id() {
        return rule_id;
    }

    public void setRule_id(String rule_id) {
        this.rule_id = rule_id;
    }

    public String getRule_name() {
        return rule_name;
    }

    public void setRule_name(String rule_name) {
        this.rule_name = rule_name;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getWindow() {
        return window;
    }

    public void setWindow(long window) {
        this.window = window;
    }

    public long getLimit() {
        return limit;
    }

    public void setLimit(long limit) {
        this.limit = limit;
    }

    public long getBlock_time() {
        return block_time;
    }

    public void setBlock_time(long block_time) {
        this.block_time = block_time;
    }

    public EnforceType getEnforce() {
        return enforce;
    }

    public void setEnforce(EnforceType enforce) {
        this.enforce = enforce;
    }

    public String getName_space() {
        return name_space;
    }

    public void setName_space(String name_space) {
        this.name_space = name_space;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public long getTtl() {
        return ttl;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    public boolean isEnforce() {
        return enforce == EnforceType.YES;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WafRateRule that = (WafRateRule) o;

        if (window != that.window) return false;
        if (limit != that.limit) return false;
        if (block_time != that.block_time) return false;
        if (rule_id != null ? !rule_id.equals(that.rule_id) : that.rule_id != null) return false;
        if (ttl != that.ttl) return false;
        if (rule_name != null ? !rule_name.equals(that.rule_name) : that.rule_name != null) return false;
        if (user_type != null ? !user_type.equals(that.user_type) : that.user_type != null) return false;
        if (path != null ? !path.equals(that.path) : that.path != null) return false;
        if (method != null ? !method.equals(that.method) : that.method != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        if (enforce != null ? !enforce.equals(that.enforce) : that.enforce != null) return false;
        if (name_space != null ? !name_space.equals(that.name_space) : that.name_space != null) return false;
        return action != null ? action.equals(that.action) : that.action == null;
    }

    @Override
    public int hashCode() {
        int result = rule_id != null ? rule_id.hashCode() : 0;
        result = 31 * result + (rule_name != null ? rule_name.hashCode() : 0);
        result = 31 * result + (user_type != null ? user_type.hashCode() : 0);
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (method != null ? method.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (int) (window ^ (window >>> 32));
        result = 31 * result + (int) (limit ^ (limit >>> 32));
        result = 31 * result + (int) (ttl ^ (ttl >>> 32));
        result = 31 * result + (int) (block_time ^ (block_time >>> 32));
        result = 31 * result + (enforce != null ? enforce.name().hashCode() : 0);
        result = 31 * result + (name_space != null ? name_space.hashCode() : 0);
        result = 31 * result + (action != null ? action.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "{" +
                "\"rule_name\":\"" + rule_name + '\"' +
                ", \"user_type\":\"" + user_type + '\"' +
                ", \"path\":\"" + path + '\"' +
                ", \"method\":\"" + method + '\"' +
                ", \"status\":\"" + status + '\"' +
                ", \"window\":\"" + window + '\"' +
                ", \"limit\":\"" + limit + '\"' +
                ", \"block_time\":\"" + block_time + '\"' +
                ", \"enforce\":\"" + enforce + '\"' +
                ", \"name_space\":\"" + name_space + '\"' +
                ", \"action\":\"" + action + '\"' +
                ", \"ttl\":\"" + ttl + '\"' +
                '}';
    }


    public boolean appliesTo(WafRequestEvent wafRequestEvent) {
        return (
                (wafRequestEvent.getPath().startsWith(this.path))
                        && (this.user_type.equals(wafRequestEvent.getUserType().name()))
                        && (this.method.equals(wafRequestEvent.getMethod()))
                        && (this.status.equals(wafRequestEvent.getStatus()))
        );
    }

    public static WafRateRule fromRawLine(JsonNode rule) {

        WafRateRuleBuilder builder = new WafRateRuleBuilder();

        builder.rule_id(rule.get("rule_id").asText());
        builder.rule_name(rule.get("rule_name").asText());
        builder.user_type(rule.get("user_type").asText());
        builder.path(rule.get("path").asText());
        builder.method(rule.get("method").asText());
        builder.status(rule.get("status").asText());
        builder.window(rule.get("window").asLong());
        builder.limit(rule.get("limit").asLong());
        builder.block_time(rule.get("block_time").asLong());
        builder.enforce(EnforceType.valueOf(rule.get("enforce").asText()));
        builder.name_space(rule.get("name_space").asText());
        builder.action(rule.get("action").asText());
        builder.ttl(rule.get("ttl").asLong());
        return builder.build();
    }

    public WafRateRule(WafRateRuleBuilder builder) {
        this(builder.rule_id, builder.rule_name, builder.user_type, builder.path, builder.method, builder.status, builder.window, builder.limit, builder.block_time, builder.enforce, builder.name_space, builder.action, builder.ttl);
    }
}
