package com.nike.artemis.broadcastProcessors;

import com.nike.artemis.LogMsgBuilder;
import com.nike.artemis.model.rules.WafRateRule;
import com.nike.artemis.model.waf.WafRequestEvent;
import com.nike.artemis.ruleChanges.WafRuleChange;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class WafRuleBroadCastProcessorFunction extends BroadcastProcessFunction<WafRequestEvent, WafRuleChange, Tuple3<String, WafRateRule, Long>> {

    static final Logger LOG = LoggerFactory.getLogger(WafRuleBroadCastProcessorFunction.class);

    private final MapStateDescriptor<WafRateRule, Object> wafRulesStateDescriptor = new MapStateDescriptor<>("WafRulesBroadcastState",
            TypeInformation.of(new TypeHint<WafRateRule>() {}), BasicTypeInfo.of(Object.class));

    @Override
    public void processElement(WafRequestEvent wafRequestEvent, BroadcastProcessFunction<WafRequestEvent, WafRuleChange,
            Tuple3<String, WafRateRule, Long>>.ReadOnlyContext ctx, Collector<Tuple3<String, WafRateRule, Long>> out) throws Exception {
        for (Map.Entry<WafRateRule, Object> entry : ctx.getBroadcastState(wafRulesStateDescriptor).immutableEntries()) {
            if (entry.getKey().appliesTo(wafRequestEvent)) {
                LOG.debug(LogMsgBuilder.getInstance()
                        .source(WafRequestEvent.class.getSimpleName())
                        .msg(String.format("matched WAF event: %s", wafRequestEvent)).toString());
                out.collect(new Tuple3<>(wafRequestEvent.getUser(), entry.getKey(), wafRequestEvent.getTime()));
            }
        }
    }

    @Override
    public void processBroadcastElement(WafRuleChange wafRuleChange, BroadcastProcessFunction<WafRequestEvent, WafRuleChange,
            Tuple3<String, WafRateRule, Long>>.Context ctx, Collector<Tuple3<String, WafRateRule, Long>> out) throws Exception {
        switch (wafRuleChange.action) {
            case CREATE:
                ctx.getBroadcastState(wafRulesStateDescriptor).put(wafRuleChange.wafRateRule, null);
                LOG.info(LogMsgBuilder.getInstance()
                        .source(WafRequestEvent.class.getSimpleName())
                        .msg(String.format("WAF RULE CREATE rule=%s", wafRuleChange.wafRateRule)).toString());
                break;
            case DELETE:
                ctx.getBroadcastState(wafRulesStateDescriptor).remove(wafRuleChange.wafRateRule);
                LOG.info(LogMsgBuilder.getInstance()
                        .source(WafRequestEvent.class.getSimpleName())
                        .msg(String.format("WAF RULE DELETE rule=%s", wafRuleChange.wafRateRule)).toString());
                break;
        }
    }
}
