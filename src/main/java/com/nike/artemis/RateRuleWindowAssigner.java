package com.nike.artemis;

import com.nike.artemis.RateRule;
import com.nike.artemis.RuleTrigger;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.WindowAssigner;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

import java.util.Collection;
import java.util.Collections;

public class RateRuleWindowAssigner extends WindowAssigner<Tuple3<String, RateRule, Long>, TimeWindow> {
    @Override
    public Collection<TimeWindow> assignWindows(Tuple3<String, RateRule, Long> element, long timestamp, WindowAssignerContext context) {
        Long startTime = element.f1.getStartTime();
        return Collections.singletonList(new TimeWindow(startTime, startTime + element.f1.getWindowSize()));
    }

    @Override
    public Trigger<Tuple3<String, RateRule, Long>, TimeWindow> getDefaultTrigger(StreamExecutionEnvironment env) {
        return new RuleTrigger();
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
