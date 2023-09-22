package com.nike.artemis.aggregators;

import com.nike.artemis.aggregators.WafRuleCountAggregate;
import com.nike.artemis.model.rules.WafRateRule;
import org.apache.flink.api.java.tuple.Tuple3;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class WafRuleCountAggregateTest {

    @Test
    public void createAccumulator_returnsZero() {
        WafRuleCountAggregate wafRuleCountAggregate = new WafRuleCountAggregate();
        assertEquals(Long.valueOf(0), wafRuleCountAggregate.createAccumulator());
    }

    @Test
    public void add_returnsPlusOne() {
        WafRuleCountAggregate wafRuleCountAggregate = new WafRuleCountAggregate();
        assertEquals(Long.valueOf(2), wafRuleCountAggregate.add(new Tuple3<>("100.100.100.100", new WafRateRule("abc", "ipaddress", "/foo/bar", "GET", "202", 0L, 0L, 0L, "YES", "checkout", "captcha"), 0L), 1L));
    }

    @Test
    public void getResult_returnTheAggregateParameter() {
        WafRuleCountAggregate wafRuleCountAggregate = new WafRuleCountAggregate();
        assertEquals(Long.valueOf(1), wafRuleCountAggregate.getResult(1L));
    }

    @Test
    public void merge_returnTheSumOfTwoAggregateParameter() throws Exception {
        WafRuleCountAggregate wafRuleCountAggregate = new WafRuleCountAggregate();
        assertEquals(Long.valueOf(3L), wafRuleCountAggregate.merge(1L, 2L));
    }
}
