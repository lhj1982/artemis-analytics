package com.nike.artemis;

import com.nike.artemis.aggregators.LaunchRuleCountAggregate;
import com.nike.artemis.model.rules.LaunchRateRule;
import org.apache.flink.api.java.tuple.Tuple4;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class LaunchRuleCountAggregateTest {

    @Test
    public void createAccumulator_returnsZero() {
        LaunchRuleCountAggregate launchRuleCountAggregator = new LaunchRuleCountAggregate();
        assertEquals(Long.valueOf(0), launchRuleCountAggregator.createAccumulator());
    }

    @Test
    public void add_returnsPlusOne() {
        LaunchRuleCountAggregate launchRuleCountAggregator = new LaunchRuleCountAggregate();
        assertEquals(Long.valueOf(2), launchRuleCountAggregator.add(new Tuple4<String, String, LaunchRateRule, Long>("100.100.100.100", UUID.randomUUID().toString(), new LaunchRateRuleBuilder().blockKind(BlockKind.upmid).limit(10L).windowSize(10L).expiration(30L).ruleState(LaunchRateRule.RuleState.ON).build(), 1L), 1L));
    }

    @Test
    public void getResult_returnTheAggregateParameter() {
        LaunchRuleCountAggregate launchRuleCountAggregator = new LaunchRuleCountAggregate();
        assertEquals(Long.valueOf(1), launchRuleCountAggregator.getResult(1L));
    }

    @Test
    public void merge_returnTheSumOfTwoAggregateParameter() throws Exception {
        LaunchRuleCountAggregate aggregate = new LaunchRuleCountAggregate();
        assertEquals(Long.valueOf(3L), aggregate.merge(1L, 2L));
    }
}
