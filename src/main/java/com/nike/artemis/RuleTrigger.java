package com.nike.artemis;

import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.triggers.TriggerResult;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

public class RuleTrigger extends Trigger<Tuple4<String, String, RateRule, Long>, TimeWindow> {
    @Override
    public TriggerResult onElement(Tuple4<String, String, RateRule, Long> element, long timestamp, TimeWindow window, TriggerContext ctx) throws Exception {
//        if (!(window.maxTimestamp() <= ctx.getCurrentWatermark())) {
//            ctx.registerEventTimeTimer(window.maxTimestamp());
//        }
        return TriggerResult.FIRE;
    }

    @Override
    public TriggerResult onProcessingTime(long time, TimeWindow window, TriggerContext ctx) throws Exception {
        return TriggerResult.CONTINUE;
    }

    @Override
    public TriggerResult onEventTime(long time, TimeWindow window, TriggerContext ctx) throws Exception {
        return TriggerResult.CONTINUE;
    }

    @Override
    public void clear(TimeWindow window, TriggerContext ctx) throws Exception {
            ctx.deleteEventTimeTimer(window.maxTimestamp());
    }
}
