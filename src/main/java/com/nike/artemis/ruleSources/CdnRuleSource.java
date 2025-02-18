package com.nike.artemis.ruleSources;

import com.nike.artemis.LogMsgBuilder;
import com.nike.artemis.model.EnforceType;
import com.nike.artemis.ruleProvider.RuleSourceProvider;
import com.nike.artemis.rulesParsers.CdnRulesParser;
import com.nike.artemis.model.rules.CdnRateRule;
import com.nike.artemis.ruleChanges.CdnRuleChange;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

public class CdnRuleSource implements SourceFunction<CdnRuleChange> {

    public static Logger LOG = LoggerFactory.getLogger(CdnRuleSource.class);
    public Boolean running = true;
    public Date currentRuleDate;
    public CdnRulesParser parser;
    public RuleSourceProvider provider;
    public HashSet<CdnRateRule> currentRules = new HashSet<>();
    public boolean testMode;

    public CdnRuleSource() {

    }

    public CdnRuleSource(RuleSourceProvider s3, boolean testMode) {
        currentRuleDate = Date.from(Instant.EPOCH);
        parser = new CdnRulesParser(s3);
        provider = s3;
        this.testMode = testMode;
    }

    @Override
    public void run(SourceContext<CdnRuleChange> ctx) {
        running = true;
        // ===================== for local cdn test purpose ====================

//        CdnRuleChange ruleChange1 = new CdnRuleChange(CdnRuleChange.Action.CREATE, new CdnRateRule("abcd", "trueClientIp", "/foo/bar", "GET|POST", "200|404", 30000L,5L, 60L, EnforceType.YES,"test_buy_checkout","captcha",90));
//        CdnRuleChange ruleChange2 = new CdnRuleChange(CdnRuleChange.Action.CREATE, new CdnRateRule("abcd", "upmid", "/foo/bar", "GET|POST", "200|404", 30000L,5L, 60L,EnforceType.NO,"test_buy_checkout","block",90));
//        CdnRuleChange[] ruleChanges = {ruleChange1, ruleChange2};
//
//        for (CdnRuleChange ruleChange : ruleChanges) {
//            System.out.println(ruleChange.cdnRateRule);
//            ctx.collect(ruleChange);
//        }

        while (running) {
            Date lastModified = provider.getLastModified();
            if (currentRuleDate.before(lastModified)) {
                Tuple2<HashSet<CdnRateRule>, Collection<CdnRuleChange>> rulesAndChanges = parser.getRulesAndChanges(currentRules);
                for (CdnRuleChange ruleChange : rulesAndChanges.f1) {
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
                Thread.currentThread().interrupt();
                LOG.warn(LogMsgBuilder.getInstance()
                        .source(CdnRateRule.class.getSimpleName())
                        .msg("generate object CdnRateRule warning")
                        .exception(e.getMessage()).toString());
            }
        }
    }

    @Override
    public void cancel() {
        running = false;
    }
}
