package com.nike.artemis.windowAssigners;

import com.nike.artemis.WindowAssigners.CdnRateRuleWindowAssigner;
import com.nike.artemis.model.EnforceType;
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
        CdnRateRule cdnRateRule = new CdnRateRule("AT-CDN-1","abc", "ipaddress", "/foo/bar/", "GET", "200", 10L, 10L, 60L, EnforceType.YES, "abc", "block", 90);
        Tuple3<String, CdnRateRule, Long> element = new Tuple3<>("100.100.100.100", cdnRateRule, 0L);
        CdnRateRuleWindowAssigner assigner = new CdnRateRuleWindowAssigner();
        Collection<TimeWindow> collection = assigner.assignWindows(element, 2, null);
        assertEquals(1, collection.size());
        assertEquals(new TimeWindow(0,600000), collection.iterator().next());
    }
}
