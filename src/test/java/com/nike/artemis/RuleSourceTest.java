package com.nike.artemis;

import com.nike.artemis.rulesParsers.CdnRulesParser;
import com.nike.artemis.rulesParsers.WafRulesParser;
import com.nike.artemis.model.rules.CdnRateRule;
import com.nike.artemis.model.rules.WafRateRule;
import com.nike.artemis.ruleChanges.CdnRuleChange;
import com.nike.artemis.ruleChanges.WafRuleChange;
import org.apache.flink.api.java.tuple.Tuple2;

import java.util.Collection;
import java.util.HashSet;

public class RuleSourceTest {
    public static void main(String[] args) {
        CdnRulesParser cdnRulesParser = new CdnRulesParser();
        HashSet<CdnRateRule> currentCdnRateRules = new HashSet<>();
        currentCdnRateRules.add(new CdnRateRule("xyz", "ip", "/buy/checkout", "GET", "200", 10L, 20L, 60L, "YES","test_buy_checkout"));
        Tuple2<HashSet<CdnRateRule>, Collection<CdnRuleChange>> rulesAndChanges = cdnRulesParser.getRulesAndChanges(currentCdnRateRules);
        System.out.println(rulesAndChanges.f0);
        System.out.println(rulesAndChanges.f1);


        WafRulesParser wafRulesParser = new WafRulesParser();
        HashSet<WafRateRule> currentWafRateRules = new HashSet<>();
        currentWafRateRules.add(new WafRateRule("waf_a_rule", "llolo", "/x/y/z", "POST", "200", 10L, 20L, 60L, "YES"));
        Tuple2<HashSet<WafRateRule>, Collection<WafRuleChange>> rulesAndChanges1 = wafRulesParser.getRulesAndChanges(currentWafRateRules);
        System.out.println(rulesAndChanges1.f0);
        System.out.println(rulesAndChanges1.f1);

    }
}
