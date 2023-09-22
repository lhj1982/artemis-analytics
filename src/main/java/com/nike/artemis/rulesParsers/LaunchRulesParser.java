package com.nike.artemis.rulesParsers;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nike.artemis.model.rules.LaunchRateRule;
import com.nike.artemis.ruleChanges.LaunchRuleChange;
import com.nike.artemis.ruleProvider.RuleSourceProvider;
import org.apache.flink.api.java.tuple.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class LaunchRulesParser implements Serializable {
    public static Logger LOG = LoggerFactory.getLogger(LaunchRulesParser.class);
    public RuleSourceProvider provider;

    public LaunchRulesParser () {
    }
    public LaunchRulesParser(RuleSourceProvider ruleSourceProvider) {
        this.provider = ruleSourceProvider;
    }

    public Tuple2<HashSet<LaunchRateRule>, Collection<LaunchRuleChange>> getRulesAndChanges(HashSet<LaunchRateRule> currentRules) {
        HashSet<LaunchRateRule> s3Rules = this.getRules();
        if (s3Rules != null) {
            Collection<LaunchRuleChange> changes = determineChanges(currentRules, s3Rules);
            return new Tuple2<>(s3Rules, changes);
        }
        return new Tuple2<>(currentRules, new ArrayList<>());
    }


     /**
     * reading rules from s3
     * @return set of rules which got from s3
     */
    private HashSet<LaunchRateRule> getRules() {
        ObjectMapper mapper = new ObjectMapper();
        HashSet<LaunchRateRule> rules = new HashSet<>();
        InputStream rulesStream = provider.getObjectContent();
        if (rulesStream == null) return rules;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(rulesStream));
        StringBuilder jsonContent = new StringBuilder();
        String line;
        try{
            while ((line = bufferedReader.readLine()) != null){
                line = line.trim();
                jsonContent.append(line);
            }
            JsonNode jsonRaw = mapper.readTree(jsonContent.toString());
            JsonNode launchRules = jsonRaw.get("LAUNCH");
            if (launchRules == null) return rules;
            if (launchRules.isArray()) {
                for (JsonNode launchRule : launchRules) {
                    LaunchRateRule rateRule = LaunchRateRule.fromRawLine(launchRule);
                    if (rateRule != null) rules.add(rateRule);
                }
            }
            return rules;
        } catch (Exception e){
            LOG.error("Location={RulesParser} source={} error={}", jsonContent, e.getMessage());
            return null;
        }

    }
    private Collection<LaunchRuleChange> determineChanges(HashSet<LaunchRateRule> currentRules, HashSet<LaunchRateRule> s3Rules) {
        List<LaunchRuleChange> changes = new ArrayList<>();
        // Determine any new rules added...
        for (LaunchRateRule r : s3Rules) {
            if ((currentRules == null) || (! currentRules.contains(r))) {
                changes.add(new LaunchRuleChange(LaunchRuleChange.Action.CREATE, r));
            }
        }

        // Emit any deleted rules
        if (currentRules != null) {
            for (LaunchRateRule r : currentRules) {
                if (!s3Rules.contains(r)) {
                    changes.add(new LaunchRuleChange(LaunchRuleChange.Action.DELETE, r));
                }
            }
        }

        return changes;
    }
}
