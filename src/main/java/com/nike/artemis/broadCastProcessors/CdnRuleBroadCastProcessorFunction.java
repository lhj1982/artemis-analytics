package com.nike.artemis.broadCastProcessors;

import com.nike.artemis.model.cdn.CdnRequestEvent;
import com.nike.artemis.model.rules.CdnRateRule;
import com.nike.artemis.ruleChanges.CdnRuleChange;
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

public class CdnRuleBroadCastProcessorFunction extends BroadcastProcessFunction<CdnRequestEvent, CdnRuleChange, Tuple3<String, CdnRateRule, Long>> {
    static final Logger LOG = LoggerFactory.getLogger(CdnRuleBroadCastProcessorFunction.class);
    private final MapStateDescriptor<CdnRateRule, Object> cdnRulesStateDescriptor = new MapStateDescriptor<>("CdnRulesBroadcastState", TypeInformation.of(new TypeHint<CdnRateRule>() {}), BasicTypeInfo.of(Object.class));

    @Override
    public void processElement(CdnRequestEvent requestEvent, BroadcastProcessFunction<CdnRequestEvent, CdnRuleChange, Tuple3<String, CdnRateRule, Long>>.ReadOnlyContext ctx, Collector<Tuple3<String, CdnRateRule, Long>> out) throws Exception {
        for (Map.Entry<CdnRateRule, Object> entry : ctx.getBroadcastState(cdnRulesStateDescriptor).immutableEntries()) {
            if (entry.getKey().appliesTo(requestEvent)) {
                LOG.info("matched CDN event: {}", requestEvent.toString());
                out.collect(new Tuple3<>(requestEvent.getUser(), entry.getKey(), requestEvent.getTime()));
            }
        }
    }

    @Override
    public void processBroadcastElement(CdnRuleChange cdnRuleChange, BroadcastProcessFunction<CdnRequestEvent, CdnRuleChange, Tuple3<String, CdnRateRule, Long>>.Context ctx, Collector<Tuple3<String, CdnRateRule, Long>> out) throws Exception {
        switch (cdnRuleChange.action) {
            case CREATE:
                ctx.getBroadcastState(cdnRulesStateDescriptor).put(cdnRuleChange.cdnRateRule, null);
                LOG.info("CDN RULE CREATE rule={}", cdnRuleChange.cdnRateRule);
                break;
            case DELETE:
                ctx.getBroadcastState(cdnRulesStateDescriptor).remove(cdnRuleChange.cdnRateRule);
                LOG.info("CDN RULE DELETE rule={}", cdnRuleChange.cdnRateRule);
                break;
        }
    }
}
