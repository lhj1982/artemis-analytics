package com.nike.artemis;

import org.apache.flink.api.java.tuple.Tuple4;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class RuleCountAggregateTest {

    @Test
    public void createAccumulator_returnsZero() {
        RuleCountAggregate ruleCountAggregator = new RuleCountAggregate();
        assertEquals(Long.valueOf(0), ruleCountAggregator.createAccumulator());
    }

    @Test
    public void add_returnsPlusOne() {
        RuleCountAggregate ruleCountAggregator = new RuleCountAggregate();
        assertEquals(Long.valueOf(2), ruleCountAggregator.add(new Tuple4<String, String, RateRule, Long>("100.100.100.100", UUID.randomUUID().toString(), new RateRuleBuilder().blockKind(BlockKind.upmid).limit(10L).windowSize(10L).expiration(30L).ruleState(RateRule.RuleState.ON).build(), 1L), 1L));
    }

    @Test
    public void getResult_returnTheAggregateParameter() {
        RuleCountAggregate ruleCountAggregator = new RuleCountAggregate();
        assertEquals(Long.valueOf(1), ruleCountAggregator.getResult(1L));
    }

    @Test
    public void merge_returnTheSumOfTwoAggregateParameter() throws Exception {
        RuleCountAggregate aggregate = new RuleCountAggregate();
        assertEquals(Long.valueOf(3L), aggregate.merge(1L, 2L));
    }
}
