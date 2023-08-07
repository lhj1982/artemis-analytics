package com.nike.artemis.ruleChanges;

import com.nike.artemis.model.rules.CdnRateRule;

public class CdnRuleChange {

    public enum Action {
        CREATE,
        DELETE
    }

    public Action action;
    public CdnRateRule cdnRateRule;
    public CdnRuleChange() {}

    public CdnRuleChange(CdnRuleChange.Action action, CdnRateRule cdnRateRule) {
        this.action = action;
        this.cdnRateRule = cdnRateRule;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CdnRuleChange that = (CdnRuleChange) o;

        if (action != that.action) return false;
        return cdnRateRule != null ? cdnRateRule.equals(that.cdnRateRule) : that.cdnRateRule == null;
    }

    @Override
    public int hashCode() {
        int result = action != null ? action.hashCode() : 0;
        result = 31 * result + (cdnRateRule != null ? cdnRateRule.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s %s",action.toString(),cdnRateRule.toString());
    }
}
