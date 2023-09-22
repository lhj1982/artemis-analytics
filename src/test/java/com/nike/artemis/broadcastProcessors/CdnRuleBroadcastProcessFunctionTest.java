package com.nike.artemis;

import com.nike.artemis.broadcastProcessors.CdnRuleBroadCastProcessorFunction;
import com.nike.artemis.model.cdn.CdnRequestEvent;
import com.nike.artemis.model.rules.CdnRateRule;
import com.nike.artemis.ruleChanges.CdnRuleChange;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.util.BroadcastOperatorTestHarness;
import org.apache.flink.streaming.util.ProcessFunctionTestHarnesses;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class CdnRuleBroadcastProcessFunctionTest {

    @Test
    public void testCdnRuleBroadcastProcessFunction() throws Exception {
        String upmid = UUID.randomUUID().toString();
        CdnRateRule cdnRateRule = new CdnRateRule("cdn_checkouts", "upmid", "/foo/checkouts", "GET", "202", 1200L, 10L, 1800L, "YES", "checkout", "block");
        CdnRequestEvent cdnEvent = new CdnRequestEvent(0L, "upmid", upmid, "GET", "/foo/checkouts/x/y/z");

        CdnRuleBroadCastProcessorFunction cdnRuleBroadCastProcessorFunction = new CdnRuleBroadCastProcessorFunction();
        MapStateDescriptor<CdnRateRule, Object> cdnRulesStateDescriptor = new MapStateDescriptor<>("CdnRulesBroadcastState", TypeInformation.of(new TypeHint<CdnRateRule>() {}), BasicTypeInfo.of(Object.class));
        BroadcastOperatorTestHarness<CdnRequestEvent, CdnRuleChange, Tuple3<String, CdnRateRule, Long>> harness = ProcessFunctionTestHarnesses.forBroadcastProcessFunction(cdnRuleBroadCastProcessorFunction, cdnRulesStateDescriptor);
        harness.open();
        harness.processBroadcastElement(new CdnRuleChange(CdnRuleChange.Action.CREATE, cdnRateRule), 0);
        harness.processElement(cdnEvent, 0);
        assertEquals(1, harness.getOutput().size());
        harness.getOutput().clear();
        harness.processBroadcastElement(new CdnRuleChange(CdnRuleChange.Action.DELETE, cdnRateRule), 1);
        harness.processElement(cdnEvent, 0);
        assertEquals(0, harness.getOutput().size());
    }
}
