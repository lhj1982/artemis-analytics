package com.nike.artemis;


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

public class RuleBroadCastProcessorFunction extends BroadcastProcessFunction<RequestEvent, RuleChange, Tuple4<String, String, RateRule, Long>> {
    public static Logger LOG = LoggerFactory.getLogger(RuleBroadCastProcessorFunction.class);
    MapStateDescriptor<RateRule, Object> rulesStateDescriptor;

    @Override
    public void open(Configuration parameters) throws Exception {
        rulesStateDescriptor = new MapStateDescriptor<>("RulesBroadcastState", TypeInformation.of(new TypeHint<RateRule>() {}), BasicTypeInfo.of(Object.class));
    }


    @Override
    public void processElement(RequestEvent requestEvent, BroadcastProcessFunction<RequestEvent, RuleChange, Tuple4<String, String, RateRule, Long>>.ReadOnlyContext ctx, Collector<Tuple4<String, String, RateRule, Long>> out) throws Exception {
        for (Map.Entry<RateRule, Object> entry : ctx.getBroadcastState(rulesStateDescriptor).immutableEntries()) {
            boolean flag = entry.getKey().appliesTo(requestEvent);
            if (flag){
                if (entry.getKey().getBlockKind().equals(BlockKind.county)){
                    out.collect(new Tuple4<>(requestEvent.getAddresses().get(0).getCounty(), requestEvent.experience.getLaunchId(),entry.getKey(), requestEvent.getTimestamp()));
                } else if (entry.getKey().getBlockKind().equals(BlockKind.ipaddress)) {
                    out.collect(new Tuple4<>(requestEvent.getDevice().getTrueClientIp(), requestEvent.experience.getLaunchId(),entry.getKey(), requestEvent.getTimestamp()));
                } else if (entry.getKey().getBlockKind().equals(BlockKind.upmid)) {
                    out.collect(new Tuple4<>(requestEvent.getUser().getUpmId(), requestEvent.experience.getLaunchId(),entry.getKey(), requestEvent.getTimestamp()));
                }
            }
        }
    }

    @Override
    public void processBroadcastElement(RuleChange value, BroadcastProcessFunction<RequestEvent, RuleChange, Tuple4<String, String, RateRule, Long>>.Context ctx, Collector<Tuple4<String, String, RateRule, Long>> out) throws Exception {
        switch (value.action) {
            case CREATE:
                LOG.info("Rule Created: {}", value.rule);
                ctx.getBroadcastState(rulesStateDescriptor).put(value.rule, null);
                break;
            case DELETE:
                LOG.info("Rule Deleted: {}", value.rule);
                ctx.getBroadcastState(rulesStateDescriptor).remove(value.rule);
                break;
        }
    }

}
