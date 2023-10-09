package com.nike.artemis.broadcastProcessors;

import com.nike.artemis.broadcastProcessors.WafRuleBroadCastProcessorFunction;
import com.nike.artemis.model.EnforceType;
import com.nike.artemis.model.rules.WafRateRule;
import com.nike.artemis.model.waf.WafRequestEvent;
import com.nike.artemis.model.waf.WafUserType;
import com.nike.artemis.ruleChanges.WafRuleChange;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.util.BroadcastOperatorTestHarness;
import org.apache.flink.streaming.util.ProcessFunctionTestHarnesses;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class WafRuleBroadcastProcessFunctionTest {

    @Test
    public void testWafRuleBroadcastProcessFunction() throws Exception {
        WafRateRule wafRateRule = new WafRateRule("waf_checkouts", "ipaddress", "/foo/checkouts", "GET", "200", 1200L, 10L, 1800L, EnforceType.YES, "checkout", "block",90);
        WafRequestEvent wafEvent = new WafRequestEvent(0L, WafUserType.ipaddress, "100.100.100.100", "GET", "/foo/checkouts/x/y/z","200");

        WafRuleBroadCastProcessorFunction wafRuleBroadCastProcessorFunction = new WafRuleBroadCastProcessorFunction();
        MapStateDescriptor<WafRateRule, Object> wafRulesStateDescriptor = new MapStateDescriptor<>("WafRulesBroadcastState", TypeInformation.of(new TypeHint<WafRateRule>() {}), BasicTypeInfo.of(Object.class));
        BroadcastOperatorTestHarness<WafRequestEvent, WafRuleChange, Tuple3<String, WafRateRule, Long>> harness = ProcessFunctionTestHarnesses.forBroadcastProcessFunction(wafRuleBroadCastProcessorFunction, wafRulesStateDescriptor);
        harness.open();
        harness.processBroadcastElement(new WafRuleChange(WafRuleChange.Action.CREATE, wafRateRule), 0);
        harness.processElement(wafEvent, 0);
        assertEquals(1, harness.getOutput().size());
        harness.getOutput().clear();
        harness.processBroadcastElement(new WafRuleChange(WafRuleChange.Action.DELETE, wafRateRule), 1);
        harness.processElement(wafEvent, 0);
        assertEquals(0, harness.getOutput().size());
    }
}
