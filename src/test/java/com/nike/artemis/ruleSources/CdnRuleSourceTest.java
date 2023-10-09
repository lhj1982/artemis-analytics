package com.nike.artemis.ruleSources;

import com.nike.artemis.ruleProvider.RuleSourceProvider;
import com.nike.artemis.ruleChanges.CdnRuleChange;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class CdnRuleSourceTest {

    class TestCdnRuleSourceProvider implements RuleSourceProvider {
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

        public TestCdnRuleSourceProvider(String rulesContents) {
            this.rulesContents = rulesContents;
        }
    }

    @Test
    public void testCdnRuleSource(){
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
                "        \"action\": \"block\",\n" +
                "        \"ttl\": 90\n" +
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
                "        \"action\": \"block\",\n" +
                "        \"ttl\": 90\n" +
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
                "        \"action\": \"block\",\n" +
                "        \"ttl\": 90\n" +
                "    }]\n" +
                "}";

        TestCdnRuleSourceProvider testCdnRuleSourceProvider = new TestCdnRuleSourceProvider(rawRules);
        CdnRuleSource ruleSource = new CdnRuleSource(testCdnRuleSourceProvider, true);
        ArrayList<CdnRuleChange> changes = new ArrayList<CdnRuleChange>();
        SourceFunction.SourceContext<CdnRuleChange> ctx = new SourceFunction.SourceContext<>() {
            @Override
            public void collect(CdnRuleChange element) {
                changes.add(element);
            }

            @Override
            public void collectWithTimestamp(CdnRuleChange element, long timestamp) {

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
        assertEquals(3, changes.size());


    }
}
