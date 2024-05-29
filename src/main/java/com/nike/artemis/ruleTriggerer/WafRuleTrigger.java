package com.nike.artemis.ruleTriggerer;

import com.nike.artemis.model.rules.WafRateRule;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.triggers.TriggerResult;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

public class WafRuleTrigger extends Trigger<Tuple4<String, WafRateRule, Long, String>, TimeWindow> {
    @Override
    public TriggerResult onElement(Tuple4<String, WafRateRule, Long, String> element, long timestamp, TimeWindow window, TriggerContext ctx) {
        return TriggerResult.FIRE;
    }

    @Override
    public TriggerResult onProcessingTime(long time, TimeWindow window, TriggerContext ctx) {
        return TriggerResult.CONTINUE;
    }

    @Override
    public TriggerResult onEventTime(long time, TimeWindow window, TriggerContext ctx) {
        return TriggerResult.PURGE;
    }

    @Override
    public void clear(TimeWindow window, TriggerContext ctx) throws Exception {
    }
}
