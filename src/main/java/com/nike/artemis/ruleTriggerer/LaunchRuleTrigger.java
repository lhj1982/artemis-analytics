package com.nike.artemis.ruleTriggerer;

import com.nike.artemis.model.rules.LaunchRateRule;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.triggers.TriggerResult;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

public class LaunchRuleTrigger extends Trigger<Tuple4<String, String, LaunchRateRule, Long>, TimeWindow> {
    @Override
    public TriggerResult onElement(Tuple4<String, String, LaunchRateRule, Long> element, long timestamp, TimeWindow window, TriggerContext ctx) {
//        if (!(window.maxTimestamp() <= ctx.getCurrentWatermark())) {
//            ctx.registerEventTimeTimer(window.maxTimestamp());
//        }
        return TriggerResult.FIRE;
    }

    @Override
    public TriggerResult onProcessingTime(long time, TimeWindow window, TriggerContext ctx) {
        return TriggerResult.CONTINUE;
    }

    @Override
    public TriggerResult onEventTime(long time, TimeWindow window, TriggerContext ctx) {
        return TriggerResult.CONTINUE;
    }

    @Override
    public void clear(TimeWindow window, TriggerContext ctx) {
        ctx.deleteEventTimeTimer(window.maxTimestamp());
    }
}
