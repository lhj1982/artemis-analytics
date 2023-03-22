package com.nike.artemis;

import com.nike.artemis.BlockKind;
import com.nike.artemis.RateRule;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.functions.source.SourceFunction;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

public class RuleSource implements SourceFunction<RuleChange> {
    public Boolean running = true;
    public Date currentRuleDate;
    public RulesParser parser;
    public RuleSourceProvider provider;
    public HashSet<RateRule> currentRules = new HashSet<>();

    public RuleSource(){
    }
    public RuleSource(RuleSourceProvider ruleSourceProvider){
        currentRuleDate = Date.from(Instant.EPOCH);
        parser = new RulesParser(ruleSourceProvider);
        provider = ruleSourceProvider;
    }

    @Override
    public void run(SourceContext<RuleChange> ctx) throws Exception {

        running = true;

        // ===================== for local test purpose ====================
        /** rule case scenario
         * - schedule bases
         * - rule: county,桃城区,,,10L,120L,2023-03-12 22:44:00.000,10L,ON,1ea61eb4-2ff5-3c36-915d-c2a91182f6c9
         * - rule: upmid,,,,10L,1L,2023-03-12 22:44:00.000,10L,ON
         * - rule: trueClientIp,,,,10L,1L,2023-03-12 22:44:00.000,10L,ON
         */
//        RuleChange ruleChange1 = new RuleChange(RuleChange.Action.CREATE, new RateRule(BlockKind.county, "桃城区", "", "", 5L, 1L, LocalDateTime.now().plusMinutes(1).toInstant(ZoneOffset.ofHours(8)).toEpochMilli(), 10L, RateRule.RuleState.ON));
//        RuleChange ruleChange2 = new RuleChange(RuleChange.Action.CREATE, new RateRule(BlockKind.trueClientIp, "", "", "", 5L, 1L, LocalDateTime.now().toInstant(ZoneOffset.ofHours(8)).toEpochMilli(), 10L, RateRule.RuleState.ON));
//        RuleChange ruleChange3 = new RuleChange(RuleChange.Action.CREATE, new RateRule(BlockKind.upmid, "", "", "", 5L, 1L, LocalDateTime.now().toInstant(ZoneOffset.ofHours(8)).toEpochMilli(), 10L, RateRule.RuleState.ON));
//        RuleChange[] ruleChanges = {ruleChange1,ruleChange2,ruleChange3};
//
//        for (RuleChange ruleChange : ruleChanges) {
//            System.out.println(ruleChange.rule);
//            ctx.collect(ruleChange);
//        }

        // ========================= reading rules from s3 =====================

        while (running){
            Date lastModified = provider.getLastModified();
            if (currentRuleDate.before(lastModified)){
                Tuple2<HashSet<RateRule>, Collection<RuleChange>> rulesAndChanges = parser.getRulesAndChanges(currentRules);
                for (RuleChange ruleChange : rulesAndChanges.f1) {
                    ctx.collect(ruleChange);
                }
                currentRules = rulesAndChanges.f0;
                currentRuleDate = lastModified;
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
