package com.nike.artemis.rulesParsers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nike.artemis.RuleSourceProvider;
import com.nike.artemis.model.rules.CdnRateRule;
import com.nike.artemis.ruleChanges.CdnRuleChange;
import org.apache.flink.api.java.tuple.Tuple2;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class CdnRulesParser implements Serializable {
    public RuleSourceProvider provider;
    public CdnRulesParser() {

    }
    public CdnRulesParser(RuleSourceProvider s3) {
        this.provider = s3;
    }

    public Tuple2<HashSet<CdnRateRule>, Collection<CdnRuleChange>> getRulesAndChanges(HashSet<CdnRateRule> currentRules) {
        HashSet<CdnRateRule> s3Rules = this.getRules();
        Collection<CdnRuleChange> changes = determineChanges(currentRules, s3Rules);
        return new Tuple2<>(s3Rules, changes);
    }


    /**
     * reading rules from s3
     * @return set of rules which got from s3
     */
    private HashSet<CdnRateRule> getRules() {
        ObjectMapper mapper = new ObjectMapper();
        HashSet<CdnRateRule> rules = new HashSet<>();
        InputStream rulesStream = provider.getObjectContent();
        if (rulesStream == null) return rules;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(rulesStream));
//        File jsonContent = new File("/Users/TTopde/Desktop/aa/artemis-analytics/src/test/resources/rules");
        StringBuilder jsonContent = new StringBuilder();
        String line;
        try{
            while ((line = bufferedReader.readLine()) != null){
                line = line.trim();
                jsonContent.append(line);
            }
            JsonNode jsonRaw = mapper.readTree(jsonContent.toString());
            JsonNode jsonRules = jsonRaw.get("CDN");
            if (jsonRules.isArray()) {
                for (JsonNode jsonRule : jsonRules) {
                    CdnRateRule cdnRateRule = mapper.treeToValue(jsonRule, CdnRateRule.class);
                    if (cdnRateRule != null) rules.add(cdnRateRule);
                }
            }
            return rules;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }
    private Collection<CdnRuleChange> determineChanges(HashSet<CdnRateRule> currentRules, HashSet<CdnRateRule> s3Rules) {
        List<CdnRuleChange> changes = new ArrayList<>();
        // Determine any new rules added...
        for (CdnRateRule r : s3Rules) {
            if ((currentRules == null) || (! currentRules.contains(r))) {
                changes.add(new CdnRuleChange(CdnRuleChange.Action.CREATE, r));
            }
        }

        // Emit any deleted rules
        if (currentRules != null) {
            for (CdnRateRule r : currentRules) {
                if (!s3Rules.contains(r)) {
                    changes.add(new CdnRuleChange(CdnRuleChange.Action.DELETE, r));
                }
            }
        }

        return changes;
    }
}
