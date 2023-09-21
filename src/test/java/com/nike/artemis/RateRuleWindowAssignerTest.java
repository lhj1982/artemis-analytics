package com.nike.artemis;

import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Collection;

public class RateRuleWindowAssignerTest {
    @Test
    public void testLaunchWindowAssigner() {
        RateRule rateRule = new RateRuleBuilder().blockKind(BlockKind.upmid).limit(10L).windowSize(10L).expiration(30L).ruleState(RateRule.RuleState.ON).build();
        Tuple4<String, String, RateRule, Long> match = new Tuple4<>("12123434-1212-459e-9c7c-4df29d4b8ccc", "deadbeef-5165-3261-8016-fed50bd23d39", rateRule, 0L);
        RateRuleWindowAssigner rateRuleWindowAssigner = new RateRuleWindowAssigner();
        Collection<TimeWindow> collection = rateRuleWindowAssigner.assignWindows(match, 5L, null);
        assertEquals(1, collection.size());
        assertEquals(new TimeWindow(0, 600000), collection.iterator().next());
    }
}
