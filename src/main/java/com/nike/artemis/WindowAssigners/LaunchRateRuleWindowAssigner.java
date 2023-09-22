package com.nike.artemis;

import com.nike.artemis.RateRule;
import com.nike.artemis.RuleTrigger;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.WindowAssigner;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;

public class LaunchRateRuleWindowAssigner extends WindowAssigner<Tuple4<String, String, LaunchRateRule, Long>, TimeWindow> {

    public static long getWindowStartTimeForRuleAndEventTime(long timeStamp, LaunchRateRule rule)
    {
        return (timeStamp / rule.getWindowSize()) * rule.getWindowSize();
    }
    @Override
    public Collection<TimeWindow> assignWindows(Tuple4<String, String, LaunchRateRule, Long> element, long timestamp, WindowAssignerContext context) {
        long startTime = getWindowStartTimeForRuleAndEventTime(timestamp, element.f2);
        return Collections.singletonList(new TimeWindow(startTime, startTime + element.f2.getWindowSize()));
    }

    @Override
    public Trigger<Tuple4<String, String, LaunchRateRule, Long>, TimeWindow> getDefaultTrigger(StreamExecutionEnvironment env) {
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
