package com.nike.artemis.rulesParsers;

import com.nike.artemis.BlockKind;
import com.nike.artemis.LaunchRateRuleBuilder;
import com.nike.artemis.model.rules.LaunchRateRule;
import com.nike.artemis.ruleChanges.LaunchRuleChange;
import com.nike.artemis.ruleProvider.RuleSourceProvider;
import com.nike.artemis.rulesParsers.LaunchRulesParser;
import org.apache.flink.api.java.tuple.Tuple2;
import static org.junit.Assert.*;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

public class LaunchRulesParserTest {

    class LaunchTestRulesProvider implements RuleSourceProvider {

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

        public LaunchTestRulesProvider (String s3LaunchRules) {
            this.rulesContents = s3LaunchRules;
        }

        public void updateLaunchRemoteRule_Delete() {
            this.rulesContents = "{\n" +
                    "  \"LAUNCH\": [\n" +
                    "    {\n" +
                    "      \"rule_id\": \"AT-LAUNCH-1\",\n" +
                    "      \"rule_name\": \"launch county block\",\n" +
                    "      \"block_kind\": \"upmid\",\n" +
                    "      \"limit\": 10,\n" +
                    "      \"window_size\": 10,\n" +
                    "      \"block_time\": 30,\n" +
                    "      \"rule_state\": \"ON\",\n" +
                    "      \"action\": \"block\"\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";
        }

        public void updateLaunchRemoteRule_Null() {
            this.rulesContents = "{\n" +
                    "  \"LAUNCH\": []\n" +
                    "}";
        }

        public void updateLaunchRemoteRule_InvalidJson() {
            this.rulesContents = "{\n" +
                    "  \"LAUNCH\": [\n" +
                    "    {\n" +
                    "      \"rule_name \"launch county block\",\n" +
                    "      \"block_kind\": \"upmid\",\n" +
                    "      \"limit\": 10,\n" +
                    "      \"window_size\": 10,\n" +
                    "      \"block_time\": 30,\n" +
                    "      \"rule_state\": \"ON\",\n" +
                    "      \"action\": \"block\"\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";
        }

        public void updateLaunchRemoteRule_NoLaunchSection() {
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
                    "      \"name_space\": \"checkout\"\n" +
                    "    }]\n" +
                    "}";
        }
    }

    @Test
    public void testParse() {
        LaunchRateRule rateRule1 = new LaunchRateRuleBuilder().ruleId("AT-LAUNCH-1").blockKind(BlockKind.upmid).limit(10L).windowSize(10L).expiration(30L).action("block").ruleState(LaunchRateRule.RuleState.ON).build();
        LaunchRateRule rateRule2 = new LaunchRateRuleBuilder().ruleId("AT-LAUNCH-2").blockKind(BlockKind.ipaddress).limit(20L).windowSize(5L).expiration(30L).action("block").ruleState(LaunchRateRule.RuleState.ON).build();
        LaunchRateRule rateRule3 = new LaunchRateRuleBuilder().ruleId("AT-LAUNCH-3").blockKind(BlockKind.county).limit(1000L).windowSize(1L).expiration(30L).action("block").ruleState(LaunchRateRule.RuleState.OFF).build();

        String s3LaunchRule = "{\n" +
                "  \"LAUNCH\": [\n" +
                "    {\n" +
                "      \"rule_id\": \"AT-LAUNCH-1\",\n" +
                "      \"rule_name\": \"launch county block\",\n" +
                "      \"block_kind\": \"upmid\",\n" +
                "      \"limit\": 10,\n" +
                "      \"window_size\": 10,\n" +
                "      \"block_time\": 30,\n" +
                "      \"rule_state\": \"ON\",\n" +
                "      \"action\": \"block\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"rule_id\": \"AT-LAUNCH-2\",\n" +
                "      \"rule_name\": \"launch trueClientIp block\",\n" +
                "      \"block_kind\": \"trueClientIp\",\n" +
                "      \"limit\": 20,\n" +
                "      \"window_size\": 5,\n" +
                "      \"block_time\": 30,\n" +
                "      \"rule_state\": \"ON\",\n" +
                "      \"action\": \"block\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"rule_id\": \"AT-LAUNCH-3\",\n" +
                "      \"rule_name\": \"launch upmid block\",\n" +
                "      \"block_kind\": \"county\",\n" +
                "      \"limit\": 1000,\n" +
                "      \"window_size\": 1,\n" +
                "      \"block_time\": 30,\n" +
                "      \"rule_state\": \"OFF\",\n" +
                "      \"action\": \"block\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        LaunchTestRulesProvider launchTestRulesProvider = new LaunchTestRulesProvider(s3LaunchRule);
        LaunchRulesParser rulesParser = new LaunchRulesParser(launchTestRulesProvider);
        Tuple2<HashSet<LaunchRateRule>, Collection<LaunchRuleChange>> rulesAndChanges = rulesParser.getRulesAndChanges(new HashSet<>());
        assertEquals(3, rulesAndChanges.f0.size());
        assertEquals(3, rulesAndChanges.f1.size());
        assertTrue(rulesAndChanges.f0.contains(rateRule1));
        assertTrue(rulesAndChanges.f0.contains(rateRule2));
        assertTrue(rulesAndChanges.f0.contains(rateRule3));
        assertTrue(rulesAndChanges.f1.contains(new LaunchRuleChange(LaunchRuleChange.Action.CREATE, rateRule1)));
        assertTrue(rulesAndChanges.f1.contains(new LaunchRuleChange(LaunchRuleChange.Action.CREATE, rateRule2)));
        assertTrue(rulesAndChanges.f1.contains(new LaunchRuleChange(LaunchRuleChange.Action.CREATE, rateRule3)));

        launchTestRulesProvider.updateLaunchRemoteRule_Delete();
        rulesAndChanges = rulesParser.getRulesAndChanges(new HashSet<>(Arrays.asList(rateRule1, rateRule2, rateRule3)));
        assertEquals(1, rulesAndChanges.f0.size());
        assertEquals(2, rulesAndChanges.f1.size());
        assertTrue(rulesAndChanges.f0.contains(rateRule1));
        assertTrue(rulesAndChanges.f1.contains(new LaunchRuleChange(LaunchRuleChange.Action.DELETE, rateRule2)));
        assertTrue(rulesAndChanges.f1.contains(new LaunchRuleChange(LaunchRuleChange.Action.DELETE, rateRule3)));


        launchTestRulesProvider.updateLaunchRemoteRule_Null();
        rulesAndChanges = rulesParser.getRulesAndChanges(new HashSet<>(Arrays.asList(rateRule1, rateRule2)));
        assertEquals(0, rulesAndChanges.f0.size());
        assertEquals(2, rulesAndChanges.f1.size());
        assertTrue(rulesAndChanges.f1.contains(new LaunchRuleChange(LaunchRuleChange.Action.DELETE, rateRule1)));
        assertTrue(rulesAndChanges.f1.contains(new LaunchRuleChange(LaunchRuleChange.Action.DELETE, rateRule2)));


        launchTestRulesProvider.updateLaunchRemoteRule_InvalidJson();
        rulesAndChanges = rulesParser.getRulesAndChanges(new HashSet<>(Arrays.asList(rateRule1, rateRule2)));
        assertEquals(2, rulesAndChanges.f0.size());
        assertEquals(0, rulesAndChanges.f1.size());
        assertTrue(rulesAndChanges.f0.contains(rateRule1));
        assertTrue(rulesAndChanges.f0.contains(rateRule2));


        launchTestRulesProvider.updateLaunchRemoteRule_NoLaunchSection();
        rulesAndChanges = rulesParser.getRulesAndChanges(new HashSet<>(Arrays.asList(rateRule1, rateRule2)));
        assertEquals(0, rulesAndChanges.f0.size());
        assertEquals(2, rulesAndChanges.f1.size());
        assertTrue(rulesAndChanges.f1.contains(new LaunchRuleChange(LaunchRuleChange.Action.DELETE, rateRule1)));
        assertTrue(rulesAndChanges.f1.contains(new LaunchRuleChange(LaunchRuleChange.Action.DELETE, rateRule2)));





    }
}
