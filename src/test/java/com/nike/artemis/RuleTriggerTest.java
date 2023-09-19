package com.nike.artemis;

import static org.junit.Assert.*;

import org.apache.flink.streaming.api.windowing.triggers.TriggerResult;
import org.junit.Test;

public class RuleTriggerTest {

    @Test
    public void onElementReturns_Fire() throws Exception {
        RuleTrigger ruleTrigger = new RuleTrigger();
        assertEquals(TriggerResult.FIRE, ruleTrigger.onElement(null, 0, null, null));
    }
}
