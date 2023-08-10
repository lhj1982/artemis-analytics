package com.nike.artemis.aggregators;

import com.nike.artemis.model.rules.WafRateRule;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.java.tuple.Tuple3;

public class WafRuleCountAggregate implements AggregateFunction<Tuple3<String, WafRateRule, Long>, Long, Long> {
    @Override
    public Long createAccumulator() {
        return 0L;
    }

    @Override
    public Long add(Tuple3<String, WafRateRule, Long> value, Long accumulator) {
        assert (accumulator != null);
        accumulator = accumulator + 1L;
        return accumulator;
    }

    @Override
    public Long getResult(Long accumulator) {
        return accumulator;
    }

    @Override
    public Long merge(Long a, Long b) {
        return a+b;
    }
}
