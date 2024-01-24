package com.nike.artemis.ruleTriggerer;

import com.nike.artemis.ruleTriggerer.CdnRuleTrigger;
import org.apache.flink.streaming.api.windowing.triggers.TriggerResult;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CdnRuleTriggerTest {
    @Test
    public void onElementReturns_Fire() throws Exception {
        CdnRuleTrigger cdnRuleTrigger = new CdnRuleTrigger();
        assertEquals(TriggerResult.FIRE, cdnRuleTrigger.onElement(null, 0, null, null));
    }

    @Test
    public void onProcessingTimeReturns_Continue() throws Exception {
        CdnRuleTrigger cdnRuleTrigger = new CdnRuleTrigger();
        assertEquals(TriggerResult.CONTINUE, cdnRuleTrigger.onProcessingTime(0,null,null));
    }

    @Test
    public void onEventTimeReturns_Continue() throws Exception {
        CdnRuleTrigger cdnRuleTrigger = new CdnRuleTrigger();
        assertEquals(TriggerResult.PURGE, cdnRuleTrigger.onEventTime(0, null, null));
    }
}
