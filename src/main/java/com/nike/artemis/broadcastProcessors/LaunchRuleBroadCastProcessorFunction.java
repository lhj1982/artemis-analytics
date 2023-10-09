package com.nike.artemis.broadcastProcessors;


import com.nike.artemis.BlockKind;
import com.nike.artemis.LogMsgBuilder;
import com.nike.artemis.model.cdn.CdnRequestEvent;
import com.nike.artemis.model.launch.LaunchRequestEvent;
import com.nike.artemis.model.rules.LaunchRateRule;
import com.nike.artemis.model.waf.WafRequestEvent;
import com.nike.artemis.ruleChanges.LaunchRuleChange;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class LaunchRuleBroadCastProcessorFunction extends BroadcastProcessFunction<LaunchRequestEvent, LaunchRuleChange,
        Tuple4<String, String, LaunchRateRule, Long>> {
    public static Logger LOG = LoggerFactory.getLogger(LaunchRuleBroadCastProcessorFunction.class);
    MapStateDescriptor<LaunchRateRule, Object> rulesStateDescriptor;

    @Override
    public void open(Configuration parameters) throws Exception {
        rulesStateDescriptor = new MapStateDescriptor<>("LaunchRulesBroadcastState",
                TypeInformation.of(new TypeHint<LaunchRateRule>() {}), BasicTypeInfo.of(Object.class));
    }


    @Override
    public void processElement(LaunchRequestEvent requestEvent, BroadcastProcessFunction<LaunchRequestEvent, LaunchRuleChange,
            Tuple4<String, String, LaunchRateRule, Long>>.ReadOnlyContext ctx, Collector<Tuple4<String, String, LaunchRateRule, Long>> out) throws Exception {
        for (Map.Entry<LaunchRateRule, Object> entry : ctx.getBroadcastState(rulesStateDescriptor).immutableEntries()) {
            boolean flag = entry.getKey().appliesTo(requestEvent);
            if (flag) {
                if (entry.getKey().getBlockKind().equals(BlockKind.county)) {
                    out.collect(new Tuple4<>(requestEvent.getAddresses().get(0).getCounty(), requestEvent.experience.getLaunchId(),
                            entry.getKey(), requestEvent.getTimestamp()));
                } else if (entry.getKey().getBlockKind().equals(BlockKind.ipaddress)) {
                    out.collect(new Tuple4<>(requestEvent.getDevice().getTrueClientIp(), requestEvent.experience.getLaunchId(),
                            entry.getKey(), requestEvent.getTimestamp()));
                } else if (entry.getKey().getBlockKind().equals(BlockKind.upmid)) {
                    out.collect(new Tuple4<>(requestEvent.getUser().getUpmId(), requestEvent.experience.getLaunchId(), entry.getKey(),
                            requestEvent.getTimestamp()));
                }
            }
        }
    }

    @Override
    public void processBroadcastElement(LaunchRuleChange value, BroadcastProcessFunction<LaunchRequestEvent, LaunchRuleChange,
            Tuple4<String, String, LaunchRateRule, Long>>.Context ctx, Collector<Tuple4<String, String, LaunchRateRule, Long>> out) throws Exception {
        switch (value.action) {
            case CREATE:
                ctx.getBroadcastState(rulesStateDescriptor).put(value.rule, null);
                LOG.info(LogMsgBuilder.getInstance()
                        .source(CdnRequestEvent.class.getSimpleName())
                        .msg(String.format("LAUNCH RULE CREATE rule=%s", value.rule))
                        .build().toString());
                break;
            case DELETE:
                ctx.getBroadcastState(rulesStateDescriptor).remove(value.rule);
                LOG.info(LogMsgBuilder.getInstance()
                        .source(WafRequestEvent.class.getSimpleName())
                        .msg(String.format("LAUNCH RULE DELETE rule=%s", value.rule))
                        .build().toString());
                break;
        }
    }
}
