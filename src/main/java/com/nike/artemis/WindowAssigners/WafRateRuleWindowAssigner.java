package com.nike.artemis.WindowAssigners;

import com.nike.artemis.model.rules.WafRateRule;
import com.nike.artemis.ruleTriggerer.WafRuleTrigger;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.WindowAssigner;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

import java.util.Collection;
import java.util.Collections;

public class WafRateRuleWindowAssigner extends WindowAssigner<Tuple3<String, WafRateRule, Long>, TimeWindow> {
    @Override
    public Collection<TimeWindow> assignWindows(Tuple3<String, WafRateRule, Long> element, long timestamp, WindowAssignerContext context) {
        long start = getWindowStartTime(timestamp, element.f1);
        return Collections.singletonList(new TimeWindow(start, start + element.f1.getWindow()));
    }

    private long getWindowStartTime(long timestamp, WafRateRule wafRateRule) {
        return (timestamp / wafRateRule.getWindow()) * wafRateRule.getWindow();
    }

    @Override
    public Trigger<Tuple3<String, WafRateRule, Long>, TimeWindow> getDefaultTrigger(StreamExecutionEnvironment env) {
        return new WafRuleTrigger();
    }

    @Override
    public TypeSerializer<TimeWindow> getWindowSerializer(ExecutionConfig executionConfig) {
        return new TimeWindow.Serializer();
    }

    @Override
    public boolean isEventTime() {
        return true;
    }
}
