package com.nike.artemis;

import com.nike.artemis.BlockKind;
import com.nike.artemis.RateRule;
import com.nike.artemis.RequestEvent;
import com.nike.artemis.RuleChange;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
import org.apache.flink.util.Collector;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.Map;

public class RuleBroadCastProcessorFunction extends BroadcastProcessFunction<RequestEvent, RuleChange, Tuple3<String, RateRule, Long>> {

    MapStateDescriptor<RateRule, Object> rulesStateDescriptor;

    @Override
    public void open(Configuration parameters) throws Exception {
        rulesStateDescriptor = new MapStateDescriptor<>("RulesBroadcastState", TypeInformation.of(new TypeHint<RateRule>() {}), BasicTypeInfo.of(Object.class));
    }


    @Override
    public void processElement(RequestEvent requestEvent, BroadcastProcessFunction<RequestEvent, RuleChange, Tuple3<String, RateRule, Long>>.ReadOnlyContext ctx, Collector<Tuple3<String, RateRule, Long>> out) throws Exception {
        for (Map.Entry<RateRule, Object> entry : ctx.getBroadcastState(rulesStateDescriptor).immutableEntries()) {
            Tuple2<BlockKind, String> tuple2 = entry.getKey().appliesTo(requestEvent);
            if (tuple2.f0 != null) {// which means it is a county
                    out.collect(new Tuple3<>(tuple2.f1, entry.getKey(), requestEvent.getTimestamp()));
            }
        }
    }

    @Override
    public void processBroadcastElement(RuleChange value, BroadcastProcessFunction<RequestEvent, RuleChange, Tuple3<String, RateRule, Long>>.Context ctx, Collector<Tuple3<String, RateRule, Long>> out) throws Exception {
        switch (value.action) {
            case CREATE:
//                System.out.println("rule created");
                ctx.getBroadcastState(rulesStateDescriptor).put(value.rule, null);
                break;
            case DELETE:
                ctx.getBroadcastState(rulesStateDescriptor).remove(value.rule);
                break;
        }
    }

}
