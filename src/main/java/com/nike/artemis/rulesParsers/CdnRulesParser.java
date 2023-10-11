package com.nike.artemis.rulesParsers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nike.artemis.LogMsgBuilder;
import com.nike.artemis.ruleProvider.RuleSourceProvider;
import com.nike.artemis.model.rules.CdnRateRule;
import com.nike.artemis.ruleChanges.CdnRuleChange;
import com.nike.artemis.ruleSources.CdnRuleSource;
import org.apache.flink.api.java.tuple.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class CdnRulesParser implements Serializable {

    public static Logger LOG = LoggerFactory.getLogger(CdnRulesParser.class);
    public RuleSourceProvider provider;

    public CdnRulesParser() {

    }

    public CdnRulesParser(RuleSourceProvider s3) {
        this.provider = s3;
    }

    public Tuple2<HashSet<CdnRateRule>, Collection<CdnRuleChange>> getRulesAndChanges(HashSet<CdnRateRule> currentRules) {
        HashSet<CdnRateRule> s3Rules = this.getRules();
        if (s3Rules != null) {
            Collection<CdnRuleChange> changes = determineChanges(currentRules, s3Rules);
            return new Tuple2<>(s3Rules, changes);
        }
        return new Tuple2<>(currentRules, new ArrayList<>());
    }


    /**
     * reading rules from s3
     *
     * @return set of rules which got from s3
     */
    private HashSet<CdnRateRule> getRules() {
        ObjectMapper mapper = new ObjectMapper();
        HashSet<CdnRateRule> rules = new HashSet<>();
        InputStream rulesStream = provider.getObjectContent();
        if (rulesStream == null) return rules;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(rulesStream));
        StringBuilder jsonContent = new StringBuilder();
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();
                jsonContent.append(line);
            }
            JsonNode jsonRaw = mapper.readTree(jsonContent.toString());
            JsonNode jsonRules = jsonRaw.get("CDN");
            if (jsonRules == null) return new HashSet<>();
            if (jsonRules.isArray()) {
                for (JsonNode jsonRule : jsonRules) {
                    CdnRateRule cdnRateRule = CdnRateRule.fromRawLine(jsonRule);
                    if (cdnRateRule != null) rules.add(cdnRateRule);
                }
            }
            return rules;
        } catch (Exception e) {
            LOG.error(LogMsgBuilder.getInstance()
                    .source(CdnRateRule.class.getSimpleName())
                    .msg("parser cdn rules from s3 failed")
                    .data(jsonContent)
                    .exception(e.getMessage())
                    .build().toString());
            return null;
        }

    }

    private Collection<CdnRuleChange> determineChanges(HashSet<CdnRateRule> currentRules, HashSet<CdnRateRule> s3Rules) {
        List<CdnRuleChange> changes = new ArrayList<>();
        // Determine any new rules added...
        for (CdnRateRule r : s3Rules) {
            if ((currentRules == null) || (!currentRules.contains(r))) {
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
