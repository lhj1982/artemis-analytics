package com.nike.artemis.ruleTriggerer;

import static org.junit.Assert.*;

import com.nike.artemis.ruleTriggerer.LaunchRuleTrigger;
import org.apache.flink.streaming.api.windowing.triggers.TriggerResult;
import org.junit.Test;

public class LaunchRuleTriggerTest {

    @Test
    public void onElementReturns_Fire() throws Exception {
        LaunchRuleTrigger ruleTrigger = new LaunchRuleTrigger();
        assertEquals(TriggerResult.FIRE, ruleTrigger.onElement(null, 0, null, null));
    }

    @Test
    public void onProcessingTimeReturns_Continue() throws Exception {
        LaunchRuleTrigger ruleTrigger = new LaunchRuleTrigger();
        assertEquals(TriggerResult.CONTINUE, ruleTrigger.onProcessingTime(0,null,null));
    }

    @Test
    public void onEventTimeReturns_Continue() throws Exception {
        LaunchRuleTrigger trigger = new LaunchRuleTrigger();
        assertEquals(TriggerResult.PURGE, trigger.onEventTime(0, null, null));
    }
}
