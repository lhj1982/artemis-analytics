package com.nike.artemis.ruleChanges;

import com.nike.artemis.model.rules.WafRateRule;

public class WafRuleChange {

    public enum Action {
        CREATE,
        DELETE
    }

    public WafRateRule wafRateRule;
    public Action action;

    public WafRuleChange() {
    }

    public WafRuleChange(WafRuleChange.Action action, WafRateRule wafRateRule) {
        this.action = action;
        this.wafRateRule = wafRateRule;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WafRuleChange that = (WafRuleChange) o;

        if (wafRateRule != null ? !wafRateRule.equals(that.wafRateRule) : that.wafRateRule != null) return false;
        return action == that.action;
    }

    @Override
    public int hashCode() {
        int result = wafRateRule != null ? wafRateRule.hashCode() : 0;
        result = 31 * result + (action != null ? action.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s %s",action.toString(), wafRateRule.toString());
    }
}
