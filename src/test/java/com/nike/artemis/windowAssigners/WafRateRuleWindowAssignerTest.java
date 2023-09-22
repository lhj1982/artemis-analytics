package com.nike.artemis.windowAssigners;

import com.nike.artemis.WindowAssigners.CdnRateRuleWindowAssigner;
import com.nike.artemis.WindowAssigners.WafRateRuleWindowAssigner;
import com.nike.artemis.model.rules.CdnRateRule;
import com.nike.artemis.model.rules.WafRateRule;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertEquals;

public class WafRateRuleWindowAssignerTest {

    @Test
    public void testWafWindowAssignerTest() {
        WafRateRule wafRateRule = new WafRateRule("abc", "ipaddress", "/foo/bar/", "GET", "200", 600L, 10L, 1200L, "YES", "abc", "block");
        Tuple3<String, WafRateRule, Long> element = new Tuple3<>("100.100.100.100", wafRateRule, 0L);
        WafRateRuleWindowAssigner assigner = new WafRateRuleWindowAssigner();
        Collection<TimeWindow> collection = assigner.assignWindows(element, 2, null);
        assertEquals(1, collection.size());
        assertEquals(new TimeWindow(0,600), collection.iterator().next());
    }
}
