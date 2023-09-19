package com.nike.artemis;

import com.nike.artemis.model.rules.WafRateRule;
import com.nike.artemis.ruleChanges.WafRuleChange;
import com.nike.artemis.rulesParsers.WafRulesParser;
import org.apache.flink.api.java.tuple.Tuple2;
import org.junit.Test;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

public class WafRulesParserTest {

    class WafTestRuleSourceProvider implements RuleSourceProvider {

        String rulesContents;
        Instant lastModified = Instant.EPOCH;
        @Override
        public Date getLastModified() {
            return Date.from(lastModified);
        }

        @Override
        public InputStream getObjectContent() {
            return new ByteArrayInputStream(rulesContents.getBytes());
        }

        public WafTestRuleSourceProvider (String rulesContents) {
            this.rulesContents = rulesContents;
        }

        public void updateWafRemoteRule_Delete() {
            this.rulesContents = "{\n" +
                    "    \"WAF\": [{\n" +
                    "      \"rule_name\": \"waf_checkouts\",\n" +
                    "      \"user_type\": \"ipaddress\",\n" +
                    "      \"path\": \"/foo/checkouts\",\n" +
                    "      \"method\": \"GET\",\n" +
                    "      \"status\": \"202\",\n" +
                    "      \"window\": 1200,\n" +
                    "      \"limit\": 10,\n" +
                    "      \"block_time\": 1800,\n" +
                    "      \"enforce\": \"YES\",\n" +
                    "      \"name_space\": \"checkout\"\n" +
                    "    }]\n" +
                    "}";
        }

        public void updateWafRemoteRuleSectionPresent_noRule() {
            this.rulesContents = "{\n" +
                    "    \"WAF\": []\n" +
                    "}";
        }

        public void updateWafRemoteRuleToInvalidJson() {
            this.rulesContents = "{\n" +
                    "    \"CDN\": [{\n" +
                    "        \"rule_name\": \"cdn_checkout_rule\",\n" +
                    "        \"user_type\": \"ipaddress\"\n" +
                    "        \"path\": \"/foo/bar/checkouts\",\n" +
                    "        \"method\": \"GET\",\n" +
                    "        \"status\": \"200\",\n" +
                    "        \"window\": 600,\n" +
                    "        \"limit\" 10,\n" +
                    "        \"block_time\": 1200,\n" +
                    "        \"enforce\": \"YES\",\n" +
                    "        \"name_space\": \"buy_checkouts\"\n" +
                    "    }]\n" +
                    "}";
        }

        public void updateToNoWAFSection() {
            this.rulesContents = "{\n" +
                    "    \"CDN\": [{\n" +
                    "      \"rule_name\": \"waf_checkouts\",\n" +
                    "      \"user_type\": \"ipaddress\",\n" +
                    "      \"path\": \"/foo/checkouts\",\n" +
                    "      \"method\": \"GET\",\n" +
                    "      \"status\": \"202\",\n" +
                    "      \"window\": 1200,\n" +
                    "      \"limit\": 10,\n" +
                    "      \"block_time\": 1800,\n" +
                    "      \"enforce\": \"YES\",\n" +
                    "      \"name_space\": \"checkout\"\n" +
                    "    }]\n" +
                    "}";
        }
    }

    @Test
    public void testWafRuleParser() {
        WafRateRule wafRateRule1 = new WafRateRule("waf_checkouts", "ipaddress", "/foo/checkouts", "GET", "202", 1200L, 10L, 1800L, "YES", "checkout");
        WafRateRule wafRateRule2 =new WafRateRule("waf_orders_history", "umid", "/foo/bar/orders", "GET", "200", 600L, 10L, 1200L, "NO", "orders");

        String wafS3Rules = "{\n" +
                "    \"WAF\": [{\n" +
                "      \"rule_name\": \"waf_checkouts\",\n" +
                "      \"user_type\": \"ipaddress\",\n" +
                "      \"path\": \"/foo/checkouts\",\n" +
                "      \"method\": \"GET\",\n" +
                "      \"status\": \"202\",\n" +
                "      \"window\": 1200,\n" +
                "      \"limit\": 10,\n" +
                "      \"block_time\": 1800,\n" +
                "      \"enforce\": \"YES\",\n" +
                "      \"name_space\": \"checkout\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"rule_name\": \"waf_orders_history\",\n" +
                "      \"user_type\": \"umid\",\n" +
                "      \"path\": \"/foo/bar/orders\",\n" +
                "      \"method\": \"GET\",\n" +
                "      \"status\": \"200\",\n" +
                "      \"window\": 600,\n" +
                "      \"limit\": 10,\n" +
                "      \"block_time\": 1200,\n" +
                "      \"enforce\": \"NO\",\n" +
                "      \"name_space\": \"orders\"\n" +
                "    }]\n" +
                "}";

        WafTestRuleSourceProvider wafTestRuleSourceProvider = new WafTestRuleSourceProvider(wafS3Rules);
        WafRulesParser wafRulesParser = new WafRulesParser(wafTestRuleSourceProvider);
        Tuple2<HashSet<WafRateRule>, Collection<WafRuleChange>> rulesAndChanges = wafRulesParser.getRulesAndChanges(null);
        assertEquals(2, rulesAndChanges.f0.size());
        assertTrue(rulesAndChanges.f0.contains(wafRateRule1));
        assertTrue(rulesAndChanges.f0.contains(wafRateRule2));
        assertEquals(2, rulesAndChanges.f1.size());
        assertTrue(rulesAndChanges.f1.contains(new WafRuleChange(WafRuleChange.Action.CREATE, wafRateRule1)));
        assertTrue(rulesAndChanges.f1.contains(new WafRuleChange(WafRuleChange.Action.CREATE, wafRateRule2)));

        wafTestRuleSourceProvider.updateWafRemoteRule_Delete();
        rulesAndChanges = wafRulesParser.getRulesAndChanges(rulesAndChanges.f0);
        assertEquals(1, rulesAndChanges.f1.size());
        assertEquals(1, rulesAndChanges.f0.size());
        assertTrue(rulesAndChanges.f0.contains(wafRateRule1));
        assertTrue(rulesAndChanges.f1.contains(new WafRuleChange(WafRuleChange.Action.DELETE, wafRateRule2)));

        wafTestRuleSourceProvider.updateWafRemoteRuleSectionPresent_noRule();
        rulesAndChanges = wafRulesParser.getRulesAndChanges(new HashSet<>(Arrays.asList(wafRateRule1)));
        assertEquals(0, rulesAndChanges.f0.size());
        assertEquals(1, rulesAndChanges.f1.size());

        wafTestRuleSourceProvider.updateWafRemoteRuleToInvalidJson();
        rulesAndChanges = wafRulesParser.getRulesAndChanges(new HashSet<>(Arrays.asList(wafRateRule1, wafRateRule2)));
        assertEquals(2, rulesAndChanges.f0.size());
        assertEquals(0, rulesAndChanges.f1.size());
        assertTrue(rulesAndChanges.f0.contains(wafRateRule1));
        assertTrue(rulesAndChanges.f0.contains(wafRateRule2));

        wafTestRuleSourceProvider.updateToNoWAFSection();
        rulesAndChanges = wafRulesParser.getRulesAndChanges(rulesAndChanges.f0);
        assertTrue(rulesAndChanges.f0.size() == 0);
        assertTrue(rulesAndChanges.f1.size() == 2);



    }
}
