package com.nike.artemis.WindowAssigners;

import com.nike.artemis.model.rules.CdnRateRule;
import com.nike.artemis.ruleTriggerer.CdnRuleTrigger;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.WindowAssigner;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

import java.util.Collection;
import java.util.Collections;

public class CdnRateRuleWindowAssigner extends WindowAssigner<Tuple3<String, CdnRateRule, Long>, TimeWindow> {

    @Override
    public Collection<TimeWindow> assignWindows(Tuple3<String, CdnRateRule, Long> element, long timestamp, WindowAssignerContext context) {
        long start = getWindowStartTime(timestamp, element.f1);
        return Collections.singletonList(new TimeWindow(start, start + element.f1.getWindow()));
    }

    private long getWindowStartTime(long timestamp, CdnRateRule rateRule) {
        return (timestamp / rateRule.getWindow()) * rateRule.getWindow();
    }

    @Override
    public Trigger<Tuple3<String, CdnRateRule, Long>, TimeWindow> getDefaultTrigger(StreamExecutionEnvironment env) {
        return new CdnRuleTrigger();
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
