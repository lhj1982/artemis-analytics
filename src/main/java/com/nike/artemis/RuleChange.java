package com.nike.artemis;

public class RuleChange {

    public enum Action {
        CREATE,
        DELETE
    }

    public Action action;
    public RateRule rule;

    public RuleChange() {
    }

    public RuleChange(RuleChange.Action action, RateRule rule) {
        this.action = action;
        this.rule = rule;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RuleChange that = (RuleChange) o;

        if (action != that.action) return false;
        return rule != null ? rule.equals(that.rule) : that.rule == null;
    }

    @Override
    public int hashCode() {
        int result = action != null ? action.hashCode() : 0;
        result = 31 * result + (rule != null ? rule.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s %s", action.toString(), rule.toString());
    }
}
