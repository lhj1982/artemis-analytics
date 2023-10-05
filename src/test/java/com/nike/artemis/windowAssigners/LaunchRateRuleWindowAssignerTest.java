package com.nike.artemis.windowAssigners;

import com.nike.artemis.BlockKind;
import com.nike.artemis.LaunchRateRuleBuilder;
import com.nike.artemis.WindowAssigners.LaunchRateRuleWindowAssigner;
import com.nike.artemis.model.rules.LaunchRateRule;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Collection;

public class LaunchRateRuleWindowAssignerTest {
    @Test
    public void testLaunchWindowAssigner() {
        LaunchRateRule launchRateRule = new LaunchRateRuleBuilder().ruleId("AT-LAUNCH-1").blockKind(BlockKind.upmid).limit(10L).windowSize(10L).expiration(30L).ruleState(LaunchRateRule.RuleState.ON).build();
        Tuple4<String, String, LaunchRateRule, Long> match = new Tuple4<>("12123434-1212-459e-9c7c-4df29d4b8ccc", "deadbeef-5165-3261-8016-fed50bd23d39", launchRateRule, 0L);
        LaunchRateRuleWindowAssigner rateRuleWindowAssigner = new LaunchRateRuleWindowAssigner();
        Collection<TimeWindow> collection = rateRuleWindowAssigner.assignWindows(match, 5L, null);
        assertEquals(1, collection.size());
        assertEquals(new TimeWindow(0, 600000), collection.iterator().next());
    }
}
