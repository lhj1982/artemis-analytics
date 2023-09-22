package com.nike.artemis.ruleSources;

import com.nike.artemis.ruleProvider.RuleSourceProvider;
import com.nike.artemis.ruleChanges.WafRuleChange;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class WafRuleSourceTest {

    class TestWafRuleSourceProvider implements RuleSourceProvider {
        String rulesContents;
        Instant lastModified = Instant.EPOCH.plusSeconds(1);

        @Override
        public Date getLastModified() {
            return Date.from(lastModified);
        }

        @Override
        public InputStream getObjectContent() {
            return new ByteArrayInputStream(rulesContents.getBytes());
        }

        public TestWafRuleSourceProvider (String rulesContents) {
            this.rulesContents = rulesContents;
        }
    }

    @Test
    public void testWafRuleSource(){
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

        TestWafRuleSourceProvider testWafRuleSourceProvider = new TestWafRuleSourceProvider(wafS3Rules);
        WafRuleSource ruleSource = new WafRuleSource(testWafRuleSourceProvider, true);
        ArrayList<WafRuleChange> changes = new ArrayList<WafRuleChange>();
        SourceFunction.SourceContext<WafRuleChange> ctx = new SourceFunction.SourceContext<>() {
            @Override
            public void collect(WafRuleChange element) {
                changes.add(element);
            }

            @Override
            public void collectWithTimestamp(WafRuleChange element, long timestamp) {

            }

            @Override
            public void emitWatermark(Watermark mark) {

            }

            @Override
            public void markAsTemporarilyIdle() {

            }

            @Override
            public Object getCheckpointLock() {
                return null;
            }

            @Override
            public void close() {

            }
        };
        ruleSource.run(ctx);
        assertEquals(2, changes.size());
    }
}
