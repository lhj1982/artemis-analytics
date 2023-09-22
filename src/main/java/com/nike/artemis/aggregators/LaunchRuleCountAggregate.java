package com.nike.artemis.aggregators;

import com.nike.artemis.model.rules.LaunchRateRule;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.java.tuple.Tuple4;

public class LaunchRuleCountAggregate implements AggregateFunction<Tuple4<String, String, LaunchRateRule, Long>, Long, Long> {
    @Override
    public Long createAccumulator() {
        return 0L;
    }

    @Override
    public Long add(Tuple4<String, String, LaunchRateRule, Long> value, Long accumulator) {
        assert (accumulator != null);
        return accumulator + 1L;
    }

    @Override
    public Long getResult(Long accumulator) {
        return accumulator;
    }

    @Override
    public Long merge(Long a, Long b) {
        return a + b;
    }
}
