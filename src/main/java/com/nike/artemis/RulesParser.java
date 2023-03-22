package com.nike.artemis;


import org.apache.flink.api.java.tuple.Tuple2;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class RulesParser implements Serializable {
    public RuleSourceProvider provider;
    public RulesParser(RuleSourceProvider ruleSourceProvider) {
        this.provider = ruleSourceProvider;
    }

    public Tuple2<HashSet<RateRule>, Collection<RuleChange>> getRulesAndChanges(HashSet<RateRule> currentRules) {
        HashSet<RateRule> s3Rules = this.getRules();
        Collection<RuleChange> changes = determineChanges(currentRules, s3Rules);
        return new Tuple2<>(s3Rules, changes);
    }


    /**
     * reading rules from s3
     * @return set of rules which got from s3
     */
    private HashSet<RateRule> getRules() {
        HashSet<RateRule> rules = new HashSet<>();
        InputStream rulesStream = provider.getObjectContent();
        if (rulesStream == null) return rules;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(rulesStream));
        String line;

        try{
            while ((line = bufferedReader.readLine()) != null){
                line = line.trim();
                String[] columns = line.split(",");
                if (columns.length > 1){
                    RateRule rateRule = RateRule.fromRawLine(columns);
                    if (rateRule != null) rules.add(rateRule);
                }
            }
            return rules;
        } catch (Exception e){
            return null;
        }

    }
    private Collection<RuleChange> determineChanges(HashSet<RateRule> currentRules, HashSet<RateRule> s3Rules) {
        List<RuleChange> changes = new ArrayList<>();
        // Determine any new rules added...
        for (RateRule r : s3Rules) {
            if ((currentRules == null) || (! currentRules.contains(r))) {
                changes.add(new RuleChange(RuleChange.Action.CREATE, r));
            }
        }

        // Emit any deleted rules
        if (currentRules != null) {
            for (RateRule r : currentRules) {
                if (!s3Rules.contains(r)) {
                    changes.add(new RuleChange(RuleChange.Action.DELETE, r));
                }
            }
        }

        return changes;
    }
}
