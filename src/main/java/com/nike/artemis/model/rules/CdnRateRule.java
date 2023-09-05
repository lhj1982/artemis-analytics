package com.nike.artemis.model.rules;

import com.nike.artemis.model.cdn.CdnRequestEvent;

public class CdnRateRule {
    private String rule_name;
    private String user_type;
    private String path;
    private String method;
    private String status;
    private long window;
    private long limit;
    private long block_time;
    private String enforce;
    private String name_space;

    public CdnRateRule() {
    }

    public CdnRateRule(String rule_name, String user_type, String path, String method, String status, long window, long limit, long block_time, String enforce, String name_space) {
        this.rule_name = rule_name;
        this.user_type = user_type;
        this.path = path;
        this.method = method;
        this.status = status;
        this.window = window;
        this.limit = limit;
        this.block_time = block_time;
        this.enforce = enforce;
        this.name_space = name_space;
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

    public String getEnforce() {
        return enforce;
    }

    public void setEnforce(String enforce) {
        this.enforce = enforce;
    }

    public String getName_space() {
        return name_space;
    }

    public void setName_space(String name_space) {
        this.name_space = name_space;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CdnRateRule that = (CdnRateRule) o;

        if (window != that.window) return false;
        if (limit != that.limit) return false;
        if (block_time != that.block_time) return false;
        if (rule_name != null ? !rule_name.equals(that.rule_name) : that.rule_name != null) return false;
        if (user_type != null ? !user_type.equals(that.user_type) : that.user_type != null) return false;
        if (path != null ? !path.equals(that.path) : that.path != null) return false;
        if (method != null ? !method.equals(that.method) : that.method != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        if (name_space != null ? !name_space.equals(that.name_space) : that.name_space != null) return false;
        return enforce != null ? enforce.equals(that.enforce) : that.enforce == null;
    }

    @Override
    public int hashCode() {
        int result = rule_name != null ? rule_name.hashCode() : 0;
        result = 31 * result + (user_type != null ? user_type.hashCode() : 0);
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (method != null ? method.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (int) (window ^ (window >>> 32));
        result = 31 * result + (int) (limit ^ (limit >>> 32));
        result = 31 * result + (int) (block_time ^ (block_time >>> 32));
        result = 31 * result + (enforce != null ? enforce.hashCode() : 0);
        result = 31 * result + (name_space != null ? name_space.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CdnRateRule{" +
                "rule_name='" + rule_name + '\'' +
                ", user_type='" + user_type + '\'' +
                ", path='" + path + '\'' +
                ", method='" + method + '\'' +
                ", status='" + status + '\'' +
                ", window=" + window +
                ", limit=" + limit +
                ", block_time=" + block_time +
                ", enforce='" + enforce + '\'' +
                ", name_space='" + name_space + '\'' +
                '}';
    }

    public boolean appliesTo(CdnRequestEvent requestEvent) {
        return (
                (requestEvent.getPath().startsWith(this.path))
                        && (requestEvent.getUserType().equals(this.user_type))
                        && (requestEvent.getMethod().equals(this.method))
        );
    }
}
