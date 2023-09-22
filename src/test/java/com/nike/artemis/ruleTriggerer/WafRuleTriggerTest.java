package com.nike.artemis.ruleTriggerer;

import com.nike.artemis.ruleTriggerer.CdnRuleTrigger;
import com.nike.artemis.ruleTriggerer.WafRuleTrigger;
import org.apache.flink.streaming.api.windowing.triggers.TriggerResult;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class WafRuleTriggerTest {

    @Test
    public void onElementReturns_Fire() throws Exception {
        WafRuleTrigger wafRuleTrigger = new WafRuleTrigger();
        assertEquals(TriggerResult.FIRE, wafRuleTrigger.onElement(null, 0, null, null));
    }

    @Test
    public void onProcessingTimeReturns_Continue() throws Exception {
        WafRuleTrigger wafRuleTrigger = new WafRuleTrigger();
        assertEquals(TriggerResult.CONTINUE, wafRuleTrigger.onProcessingTime(0,null,null));
    }

    @Test
    public void onEventTimeReturns_Continue() throws Exception {
        WafRuleTrigger wafRuleTrigger = new WafRuleTrigger();
        assertEquals(TriggerResult.CONTINUE, wafRuleTrigger.onEventTime(0, null, null));
    }

}
