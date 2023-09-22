package com.nike.artemis.ruleSources;

import com.nike.artemis.ruleProvider.RuleSourceProvider;
import com.nike.artemis.rulesParsers.WafRulesParser;
import com.nike.artemis.model.rules.WafRateRule;
import com.nike.artemis.ruleChanges.WafRuleChange;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

public class WafRuleSource implements SourceFunction<WafRuleChange>, Serializable {
    public static Logger LOG = LoggerFactory.getLogger(WafRuleSource.class);
    public Boolean running = true;
    public Date currentRuleDate;
    public WafRulesParser parser;
    public RuleSourceProvider provider;
    public HashSet<WafRateRule> currentRules = new HashSet<>();
    public boolean testMode;

    public WafRuleSource() {
    }

    public WafRuleSource(RuleSourceProvider s3, boolean testMode) {
        currentRuleDate = Date.from(Instant.EPOCH);
        parser = new WafRulesParser(s3);
        provider = s3;
        this.testMode = testMode;
    }

    @Override
    public void run(SourceContext<WafRuleChange> ctx) {
        running = true;
        while (running) {
            Date lastModified = provider.getLastModified();
            if (currentRuleDate.before(lastModified)) {
                Tuple2<HashSet<WafRateRule>, Collection<WafRuleChange>> rulesAndChanges = parser.getRulesAndChanges(currentRules);
                for (WafRuleChange ruleChange : rulesAndChanges.f1) {
                    ctx.collect(ruleChange);
                }
                currentRules = rulesAndChanges.f0;
                currentRuleDate = lastModified;
            }
            if (testMode) {
                running = false;
                break;
            }
            try {
                Thread.sleep(60*1000);
            } catch (InterruptedException ignored) {

            }
        }
    }

    @Override
    public void cancel() {
        running = false;
    }
}
