package com.nike.artemis.aggregators;

import com.nike.artemis.model.rules.WafRateRule;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.java.tuple.Tuple4;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

public class WafRuleUmidCountAggregate implements AggregateFunction<Tuple4<String, WafRateRule, Long, String>, LinkedHashSet<String>, LinkedHashSet<String>> {

    @Override
    public LinkedHashSet<String> createAccumulator() {
        return new LinkedHashSet<>();
    }

    @Override
    public LinkedHashSet<String> add(Tuple4<String, WafRateRule, Long, String> value, LinkedHashSet<String> set) {
        set.add(value.f3);
        return set;
    }

    @Override
    public LinkedHashSet<String> getResult(LinkedHashSet<String> set) {
        return set;
    }

    @Override
    public LinkedHashSet<String> merge(LinkedHashSet<String> strings, LinkedHashSet<String> acc1) {
        return null;
    }
}
