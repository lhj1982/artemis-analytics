package com.nike.artemis.ruleSources;

import com.nike.artemis.ruleChanges.LaunchRuleChange;
import com.nike.artemis.ruleProvider.RuleSourceProvider;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.watermark.Watermark;
import static org.junit.Assert.*;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

public class LaunchRuleSourceTest {

    class TestLaunchRuleSourceProvider implements RuleSourceProvider {
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

        public TestLaunchRuleSourceProvider (String rulesContents) {
            this.rulesContents = rulesContents;
        }
    }

    @Test
    public void testLaunchRuleSource(){
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

        TestLaunchRuleSourceProvider testLaunchRuleSourceProvider = new TestLaunchRuleSourceProvider(s3LaunchRule);
        LaunchRuleSource launchRuleSource = new LaunchRuleSource(testLaunchRuleSourceProvider, true);
        ArrayList<LaunchRuleChange> changes = new ArrayList<LaunchRuleChange>();
        SourceFunction.SourceContext<LaunchRuleChange> ctx = new SourceFunction.SourceContext<>() {
            @Override
            public void collect(LaunchRuleChange element) {
                changes.add(element);
            }

            @Override
            public void collectWithTimestamp(LaunchRuleChange element, long timestamp) {

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
        launchRuleSource.run(ctx);
        assertEquals(3, changes.size());


    }
}
