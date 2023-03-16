package com.nike.artemis;

import com.nike.artemis.RateRule;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.java.tuple.Tuple3;

public class RuleCountAggregator implements AggregateFunction<Tuple3<String, RateRule, Long>, Long, Long> {
    @Override
    public Long createAccumulator() {
        return 0L;
    }

    @Override
    public Long add(Tuple3<String, RateRule, Long> value, Long accumulator) {
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
