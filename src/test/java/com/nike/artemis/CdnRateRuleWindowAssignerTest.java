package com.nike.artemis;

import com.nike.artemis.WindowAssigners.CdnRateRuleWindowAssigner;
import com.nike.artemis.model.rules.CdnRateRule;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Collection;
import java.util.UUID;

public class CdnRateRuleWindowAssignerTest {
    @Test
    public void testCdnWindowAssignerTest() {
        CdnRateRule cdnRateRule = new CdnRateRule("abc", "ipaddress", "/foo/bar/", "GET", "200", 600L, 10L, 1200L, "YES", "abc", "block");
        Tuple3<String, CdnRateRule, Long> element = new Tuple3<>("100.100.100.100", cdnRateRule, 0L);
        CdnRateRuleWindowAssigner assigner = new CdnRateRuleWindowAssigner();
        Collection<TimeWindow> collection = assigner.assignWindows(element, 2, null);
        assertEquals(1, collection.size());
        assertEquals(new TimeWindow(0,600), collection.iterator().next());
    }
}
