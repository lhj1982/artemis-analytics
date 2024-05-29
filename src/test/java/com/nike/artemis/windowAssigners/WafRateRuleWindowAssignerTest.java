package com.nike.artemis.windowAssigners;

import com.nike.artemis.WindowAssigners.WafRateRuleWindowAssigner;
import com.nike.artemis.model.EnforceType;
import com.nike.artemis.model.rules.WafRateRule;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertEquals;

public class WafRateRuleWindowAssignerTest {

    @Test
    public void testWafWindowAssignerTest() {
        WafRateRule wafRateRule = new WafRateRule("AT-WAF-1", "abc", "ipaddress", "/foo/bar/", "GET", "200", 10L, 10L, 60L, EnforceType.YES, "abc", "block",90);
        Tuple4<String, WafRateRule, Long, String> element = new Tuple4<>("100.100.100.100", wafRateRule, 0L, "123");
        WafRateRuleWindowAssigner assigner = new WafRateRuleWindowAssigner();
        Collection<TimeWindow> collection = assigner.assignWindows(element, 2, null);
        assertEquals(1, collection.size());
        assertEquals(new TimeWindow(0,600000), collection.iterator().next());
    }
}
