package com.nike.artemis;

import com.nike.artemis.model.*;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.streaming.util.BroadcastOperatorTestHarness;
import org.apache.flink.streaming.util.ProcessFunctionTestHarnesses;
import static org.junit.Assert.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class RuleBroadcastProcessFunctionTest {

    @Test
    public void testLaunchRuleBroadcastProcessFunction() throws Exception {
        RateRule rateRule = new RateRuleBuilder().blockKind(BlockKind.upmid).limit(10L).windowSize(10L).expiration(30L).ruleState(RateRule.RuleState.ON).build();
        RequestEvent launchSnsEvent = new RequestEvent.Builder()
                .addresses(new ArrayList<>(Arrays.asList(new Address("上海马戏城", "上海市", "闸北区", "CN-51", null, "XA"))))
                .user(new User("12123434-1212-459e-9c7c-4df29d4b8ccc"))
                .device(new Device("136.226.234.199"))
                .experience(new Experience("com.nike.commerce.omega.droid", "deadbeef-5165-3261-8016-fed50bd23d39", "E2133C98-C974-4F46-BF40-1E7BBD28398A"))
                .entityId("E2133C98-C974-4F46-BF40-1E7BBD28398A")
                .extras(new Extras("launchentryvalidator", "6733897b-6", true, "XA"))
                .timestamp(1694063973265L)
                .build();
        RuleBroadCastProcessorFunction ruleBroadCastProcessorFunction = new RuleBroadCastProcessorFunction();
        MapStateDescriptor<RateRule, Object> rulesStateDescriptor = new MapStateDescriptor<>("RulesBroadcastState", TypeInformation.of(new TypeHint<RateRule>() {}), BasicTypeInfo.of(Object.class));
        BroadcastOperatorTestHarness<RequestEvent, RuleChange, Tuple4<String, String, RateRule, Long>> harness = ProcessFunctionTestHarnesses.forBroadcastProcessFunction(ruleBroadCastProcessorFunction, rulesStateDescriptor);
        harness.open();
        harness.processBroadcastElement(new RuleChange(RuleChange.Action.CREATE, rateRule), 0);
        harness.processElement(launchSnsEvent, 0);
        assertEquals(1, harness.getOutput().size());
        harness.getOutput().clear();
        harness.processBroadcastElement(new RuleChange(RuleChange.Action.DELETE, rateRule), 1);
        harness.processElement(launchSnsEvent, 0);
        assertEquals(0, harness.getOutput().size());
    }
}
