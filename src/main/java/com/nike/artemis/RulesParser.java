package com.nike.artemis;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.java.tuple.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class RulesParser implements Serializable {
    public static Logger LOG = LoggerFactory.getLogger(RulesParser.class);
    public RuleSourceProvider provider;

    public RulesParser () {
    }
    public RulesParser(RuleSourceProvider ruleSourceProvider) {
        this.provider = ruleSourceProvider;
    }

    public Tuple2<HashSet<RateRule>, Collection<RuleChange>> getRulesAndChanges(HashSet<RateRule> currentRules) {
        HashSet<RateRule> s3Rules = this.getRules();
        if (s3Rules != null) {
            Collection<RuleChange> changes = determineChanges(currentRules, s3Rules);
            return new Tuple2<>(s3Rules, changes);
        }
        return new Tuple2<>(currentRules, new ArrayList<>());
    }


     /**
     * reading rules from s3
     * @return set of rules which got from s3
     */
    private HashSet<RateRule> getRules() {
        ObjectMapper mapper = new ObjectMapper();
        HashSet<RateRule> rules = new HashSet<>();
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
                    RateRule rateRule = RateRule.fromRawLine(launchRule);
                    if (rateRule != null) rules.add(rateRule);
                }
            }
            return rules;
        } catch (Exception e){
            LOG.error("Location={RulesParser} source={} error={}", jsonContent, e.getMessage());
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
