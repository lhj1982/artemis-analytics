package com.nike.artemis.rulesParsers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nike.artemis.ruleProvider.RuleSourceProvider;
import com.nike.artemis.model.rules.WafRateRule;
import com.nike.artemis.ruleChanges.WafRuleChange;
import org.apache.flink.api.java.tuple.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class WafRulesParser implements Serializable {
    public static Logger LOG = LoggerFactory.getLogger(WafRulesParser.class);
    public RuleSourceProvider provider;

    public WafRulesParser() {
    }
    public WafRulesParser(RuleSourceProvider s3) {
        this.provider = s3;
    }

    public Tuple2<HashSet<WafRateRule>, Collection<WafRuleChange>> getRulesAndChanges(HashSet<WafRateRule> currentRules) {
        HashSet<WafRateRule> s3Rules = this.getRules();
        if (s3Rules != null){
            Collection<WafRuleChange> changes = determineChanges(currentRules, s3Rules);
            return new Tuple2<>(s3Rules, changes);
        }
        return new Tuple2<>(currentRules, new ArrayList<>());
    }



    private HashSet<WafRateRule> getRules() {
        ObjectMapper mapper = new ObjectMapper();
        HashSet<WafRateRule> rules = new HashSet<>();
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
            JsonNode jsonRules = jsonRaw.get("WAF");
            if ( jsonRules == null ) return new HashSet<>();
            if (jsonRules.isArray()) {
                for (JsonNode jsonRule : jsonRules) {
                    WafRateRule wafRateRule = mapper.treeToValue(jsonRule, WafRateRule.class);
                    if (wafRateRule != null) rules.add(wafRateRule);
                }
            }
            return rules;
        } catch (Exception e){
            LOG.error("Location={WafRulesParser} source={} error={}", jsonContent, e.getMessage());
            return null;
        }
    }
    private Collection<WafRuleChange> determineChanges(HashSet<WafRateRule> currentRules, HashSet<WafRateRule> s3Rules) {
        List<WafRuleChange> changes = new ArrayList<>();
        // Determine any new rules added...
        for (WafRateRule r : s3Rules) {
            if ((currentRules == null) || (! currentRules.contains(r))) {
                changes.add(new WafRuleChange(WafRuleChange.Action.CREATE, r));
            }
        }

        // Emit any deleted rules
        if (currentRules != null) {
            for (WafRateRule r : currentRules) {
                if (!s3Rules.contains(r)) {
                    changes.add(new WafRuleChange(WafRuleChange.Action.DELETE, r));
                }
            }
        }

        return changes;
    }
}
