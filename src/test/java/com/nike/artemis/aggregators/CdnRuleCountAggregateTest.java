package com.nike.artemis.aggregators;

import com.nike.artemis.aggregators.CdnRuleCountAggregate;
import com.nike.artemis.model.rules.CdnRateRule;
import org.apache.flink.api.java.tuple.Tuple3;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class CdnRuleCountAggregateTest {

    @Test
    public void createAccumulator_returnsZero() {
        CdnRuleCountAggregate cdnRuleCountAggregate = new CdnRuleCountAggregate();
        assertEquals(Long.valueOf(0), cdnRuleCountAggregate.createAccumulator());
    }

    @Test
    public void add_returnsPlusOne() {
        CdnRuleCountAggregate cdnRuleCountAggregate = new CdnRuleCountAggregate();
        assertEquals(Long.valueOf(2), cdnRuleCountAggregate.add(new Tuple3<>(UUID.randomUUID().toString(), new CdnRateRule("a_b_c", "upmid", "/foo/bar/", "GET", "200", 0L, 0L, 0L, "YES", "abc", "block"), 0L), 1L));
    }

    @Test
    public void getResult_returnTheAggregateParameter() {
        CdnRuleCountAggregate cdnRuleCountAggregate = new CdnRuleCountAggregate();
        assertEquals(Long.valueOf(1), cdnRuleCountAggregate.getResult(1L));
    }

    @Test
    public void merge_returnTheSumOfTwoAggregateParameter() throws Exception {
        CdnRuleCountAggregate cdnRuleCountAggregate = new CdnRuleCountAggregate();
        assertEquals(Long.valueOf(3L), cdnRuleCountAggregate.merge(1L, 2L));
    }

}
