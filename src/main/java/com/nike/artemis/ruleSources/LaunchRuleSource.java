package com.nike.artemis.ruleSources;

import com.nike.artemis.LogMsgBuilder;
import com.nike.artemis.model.rules.LaunchRateRule;
import com.nike.artemis.ruleChanges.LaunchRuleChange;
import com.nike.artemis.ruleProvider.RuleSourceProvider;
import com.nike.artemis.rulesParsers.LaunchRulesParser;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

public class LaunchRuleSource implements SourceFunction<LaunchRuleChange>, Serializable {
    public static Logger LOG = LoggerFactory.getLogger(LaunchRuleSource.class);
    public Boolean running = true;
    public Date currentRuleDate;
    public LaunchRulesParser parser;
    public RuleSourceProvider provider;
    public HashSet<LaunchRateRule> currentRules = new HashSet<>();
    public boolean testMode;

    public LaunchRuleSource() {
    }

    public LaunchRuleSource(RuleSourceProvider ruleSourceProvider, boolean testMode) {
        currentRuleDate = Date.from(Instant.EPOCH);
        parser = new LaunchRulesParser(ruleSourceProvider);
        provider = ruleSourceProvider;
        this.testMode = testMode;
    }

    @Override
    public void run(SourceContext<LaunchRuleChange> ctx) {

        running = true;

        // ===================== for local test purpose ====================
        /** rule case scenario
         * - schedule bases
         * - rule: county,桃城区,,,10L,120L,2023-03-12 22:44:00.000,10L,ON,1ea61eb4-2ff5-3c36-915d-c2a91182f6c9
         * - rule: upmid,,,,10L,1L,2023-03-12 22:44:00.000,10L,ON
         * - rule: trueClientIp,,,,10L,1L,2023-03-12 22:44:00.000,10L,ON
         */
//        RuleChange ruleChange1 = new RuleChange(RuleChange.Action.CREATE, new RateRule(BlockKind.county, "桃城区", "", "", 5L, 1L, LocalDateTime.now().plusMinutes(1).toInstant(ZoneOffset.ofHours(8)).toEpochMilli(), 10L, RateRule.RuleState.ON));
//        RuleChange ruleChange2 = new RuleChange(RuleChange.Action.CREATE, new RateRule(BlockKind.ipaddress, "", "", "", 5L, 1L, LocalDateTime.now().toInstant(ZoneOffset.ofHours(8)).toEpochMilli(), 10L, RateRule.RuleState.ON));
//        RuleChange ruleChange3 = new RuleChange(RuleChange.Action.CREATE, new RateRule(BlockKind.upmid, "", "", "", 5L, 1L, LocalDateTime.now().toInstant(ZoneOffset.ofHours(8)).toEpochMilli(), 10L, RateRule.RuleState.ON));
//        RuleChange[] ruleChanges = {ruleChange1,ruleChange2,ruleChange3};
//
//        for (RuleChange ruleChange : ruleChanges) {
//            System.out.println(ruleChange.rule);
//            ctx.collect(ruleChange);
//        }

        // ========================= reading rules from s3 =====================

        while (running) {
            Date lastModified = provider.getLastModified();
            if (currentRuleDate.before(lastModified)) {
                Tuple2<HashSet<LaunchRateRule>, Collection<LaunchRuleChange>> rulesAndChanges = parser.getRulesAndChanges(currentRules);
                for (LaunchRuleChange ruleChange : rulesAndChanges.f1) {
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
                Thread.sleep(60 * 1000);
            } catch (InterruptedException e) {
                LOG.error(LogMsgBuilder.getInstance()
                        .source(LaunchRateRule.class.getSimpleName())
                        .msg("generate object LaunchRateRule failed")
                        .exception(e)
                        .build().toString());
            }

        }


    }

    @Override
    public void cancel() {
        running = false;
    }
}
