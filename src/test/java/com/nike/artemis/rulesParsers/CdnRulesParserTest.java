package com.nike.artemis.rulesParsers;

import static org.junit.Assert.*;

import com.nike.artemis.model.EnforceType;
import com.nike.artemis.model.rules.CdnRateRule;
import com.nike.artemis.ruleChanges.CdnRuleChange;
import com.nike.artemis.ruleProvider.RuleSourceProvider;
import com.nike.artemis.rulesParsers.CdnRulesParser;
import org.apache.flink.api.java.tuple.Tuple2;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Instant;
import java.util.*;

public class CdnRulesParserTest {

    class CdnTestRulesProvider implements RuleSourceProvider {

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

        public CdnTestRulesProvider (String rulesContents) {
            this.rulesContents = rulesContents;
        }

        public void updateCdnRemoteRule_Delete() {
            this.rulesContents = "{\n" +
                    "    \"CDN\": [{\n" +
                    "        \"rule_id\": \"AT-CDN-3\",\n" +
                    "        \"rule_name\": \"cdn_payments_rule\",\n" +
                    "        \"user_type\": \"ipaddress\",\n" +
                    "        \"path\": \"/foo/bar/payments\",\n" +
                    "        \"method\": \"POST\",\n" +
                    "        \"status\": \"204\",\n" +
                    "        \"window\": 1200,\n" +
                    "        \"limit\": 15,\n" +
                    "        \"block_time\": 600,\n" +
                    "        \"enforce\": \"YES\",\n" +
                    "        \"name_space\": \"buy_payments\",\n" +
                    "        \"ttl\": 90,\n" +
                    "        \"action\": \"block\"\n" +
                    "    }]\n" +
                    "}";
        }

        public void updateCdnRemoteRule_Null() {
            this.rulesContents = "{\n" +
                    "    \"CDN\": []\n" +
                    "}";
        }

        public void updateCdnRemoteRuleToInvalidJson() {
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
                    "        \"ttl\": 90,\n" +
                    "        \"name_space\": \"buy_checkouts\"\n" +
                    "    }]\n" +
                    "}";
        }

        public void updateToNoCDNSection() {
            this.rulesContents = "{\n" +
                    "    \"WAF\": [{\n" +
                    "      \"rule_id\": \"AT-WAF-1\",\n" +
                    "      \"rule_name\": \"waf_checkouts\",\n" +
                    "      \"user_type\": \"ipaddress\",\n" +
                    "      \"path\": \"/foo/checkouts\",\n" +
                    "      \"method\": \"GET\",\n" +
                    "      \"status\": \"202\",\n" +
                    "      \"window\": 1200,\n" +
                    "      \"limit\": 10,\n" +
                    "      \"block_time\": 1800,\n" +
                    "      \"enforce\": \"YES\",\n" +
                    "      \"name_space\": \"checkout\",\n" +
                    "      \"ttl\": 90,\n" +
                    "      \"action\": \"block\"\n" +
                    "    }]\n" +
                    "}";
        }
    }

    @Test
    public void testParser() {
        CdnRateRule cdnRateRule1 = new CdnRateRule("AT-CDN-1", "cdn_checkout_rule", "ipaddress", "/foo/bar/checkouts", "GET", "200", 600L, 10L, 1200L, EnforceType.YES, "buy_checkouts", "block",90);
        CdnRateRule cdnRateRule2 = new CdnRateRule("AT-CDN-2", "cdn_orders_history_rule", "upmid", "/foo/orders/history", "GET", "200", 300L, 20L, 1200L, EnforceType.NO, "buy_orders_history", "block",90);
        CdnRateRule cdnRateRule3 = new CdnRateRule("AT-CDN-3", "cdn_payments_rule", "ipaddress", "/foo/bar/payments", "POST", "204", 1200L, 15L, 600L, EnforceType.YES, "buy_payments", "block",90);

        String rawRules = "{\n" +
                "    \"CDN\": [{\n" +
                "        \"rule_id\": \"AT-CDN-1\",\n" +
                "        \"rule_name\": \"cdn_checkout_rule\",\n" +
                "        \"user_type\": \"ipaddress\",\n" +
                "        \"path\": \"/foo/bar/checkouts\",\n" +
                "        \"method\": \"GET\",\n" +
                "        \"status\": \"200\",\n" +
                "        \"window\": 600,\n" +
                "        \"limit\": 10,\n" +
                "        \"block_time\": 1200,\n" +
                "        \"enforce\": \"YES\",\n" +
                "        \"name_space\": \"buy_checkouts\",\n" +
                "        \"ttl\": 90,\n" +
                "        \"action\": \"block\"\n" +
                "    }, {\n" +
                "        \"rule_id\": \"AT-CDN-2\",\n" +
                "        \"rule_name\": \"cdn_orders_history_rule\",\n" +
                "        \"user_type\": \"upmid\",\n" +
                "        \"path\": \"/foo/orders/history\",\n" +
                "        \"method\": \"GET\",\n" +
                "        \"status\": \"200\",\n" +
                "        \"window\": 300,\n" +
                "        \"limit\": 20,\n" +
                "        \"block_time\": 1200,\n" +
                "        \"enforce\": \"NO\",\n" +
                "        \"name_space\": \"buy_orders_history\",\n" +
                "        \"ttl\": 90,\n" +
                "        \"action\": \"block\"\n" +
                "    }, {\n" +
                "        \"rule_id\": \"AT-CDN-3\",\n" +
                "        \"rule_name\": \"cdn_payments_rule\",\n" +
                "        \"user_type\": \"ipaddress\",\n" +
                "        \"path\": \"/foo/bar/payments\",\n" +
                "        \"method\": \"POST\",\n" +
                "        \"status\": \"204\",\n" +
                "        \"window\": 1200,\n" +
                "        \"limit\": 15,\n" +
                "        \"block_time\": 600,\n" +
                "        \"enforce\": \"YES\",\n" +
                "        \"name_space\": \"buy_payments\",\n" +
                "        \"ttl\": 90,\n" +
                "        \"action\": \"block\"\n" +
                "    }]\n" +
                "}";

        CdnTestRulesProvider cdnTestRulesProvider = new CdnTestRulesProvider(rawRules);
        CdnRulesParser rulesParser = new CdnRulesParser(cdnTestRulesProvider);
        Tuple2<HashSet<CdnRateRule>, Collection<CdnRuleChange>> rulesAndChanges = rulesParser.getRulesAndChanges(null);
        assertEquals(3, rulesAndChanges.f0.size());
        assertTrue(rulesAndChanges.f0.contains(cdnRateRule1));
        assertTrue(rulesAndChanges.f0.contains(cdnRateRule2));
        assertEquals(3, rulesAndChanges.f1.size());
        assertTrue(rulesAndChanges.f1.contains(new CdnRuleChange(CdnRuleChange.Action.CREATE, cdnRateRule1)));
        assertTrue(rulesAndChanges.f1.contains(new CdnRuleChange(CdnRuleChange.Action.CREATE, cdnRateRule2)));

        cdnTestRulesProvider.updateCdnRemoteRule_Delete();
        rulesAndChanges = rulesParser.getRulesAndChanges(rulesAndChanges.f0);
        assertEquals(1, rulesAndChanges.f0.size());
        assertEquals(2, rulesAndChanges.f1.size());
        assertTrue(rulesAndChanges.f0.contains(cdnRateRule3));
        assertTrue(rulesAndChanges.f1.contains(new CdnRuleChange(CdnRuleChange.Action.DELETE, cdnRateRule1)));
        assertTrue(rulesAndChanges.f1.contains(new CdnRuleChange(CdnRuleChange.Action.DELETE, cdnRateRule2)));


        cdnTestRulesProvider.updateCdnRemoteRule_Null();
        rulesAndChanges = rulesParser.getRulesAndChanges(new HashSet<>(Arrays.asList(cdnRateRule1)));
        assertTrue(rulesAndChanges.f0.size() == 0);
        assertTrue(rulesAndChanges.f1.size() == 1);
        assertTrue(rulesAndChanges.f1.contains(new CdnRuleChange(CdnRuleChange.Action.DELETE, cdnRateRule1)));


        cdnTestRulesProvider.updateCdnRemoteRuleToInvalidJson();
        rulesAndChanges = rulesParser.getRulesAndChanges(new HashSet<>(Arrays.asList(cdnRateRule1, cdnRateRule2)));
        assertTrue(rulesAndChanges.f0.size() == 2);
        assertTrue(rulesAndChanges.f1.size() == 0);

        cdnTestRulesProvider.updateToNoCDNSection();
        rulesAndChanges = rulesParser.getRulesAndChanges(new HashSet<>(Arrays.asList(cdnRateRule2)));
        assertTrue(rulesAndChanges.f0.size() == 0);
        assertTrue(rulesAndChanges.f1.size() == 1);
    }
}
