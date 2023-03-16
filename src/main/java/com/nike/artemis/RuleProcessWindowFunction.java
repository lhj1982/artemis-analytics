package com.nike.artemis;

import com.nike.artemis.BlockEvent;
import com.nike.artemis.RateRule;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class RuleProcessWindowFunction extends ProcessWindowFunction<Long, BlockEvent, Tuple2<String, RateRule>, TimeWindow> {

    ValueStateDescriptor<Long> currentMaxBlockDescriptor;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        currentMaxBlockDescriptor = new ValueStateDescriptor<>("maxBlockByBlockKindAndRule", Long.class);
    }

    @Override
    public void process(Tuple2<String, RateRule> stringRateRuleTuple2, ProcessWindowFunction<Long, BlockEvent, Tuple2<String, RateRule>, TimeWindow>.Context context, Iterable<Long> elements, Collector<BlockEvent> out) throws Exception {
        String blockEntity = stringRateRuleTuple2.f0;
        RateRule rateRule = stringRateRuleTuple2.f1;
        if ((elements==null) || (! elements.iterator().hasNext()))
            return;
        Long count = elements.iterator().next();

        ValueState<Long> maxBlockState = context.globalState().getState(currentMaxBlockDescriptor);
        if (maxBlockState.value() == null) {
            context.globalState().getState(currentMaxBlockDescriptor).update(0L);
        }

        long currentMaxBlock = maxBlockState.value();

        if (count >= rateRule.getLimit()){

            long newBlockEnd = context.window().getStart() + rateRule.getExpiration();
            if ((currentMaxBlock < newBlockEnd) && rateRule.isEnforce()){
                out.collect(new BlockEvent(rateRule.getBlockKind().name(), blockEntity,  LocalDateTime.now().toInstant(ZoneOffset.ofHours(8)).toEpochMilli(), newBlockEnd, rateRule.toString()));
                maxBlockState.update(newBlockEnd);
            }
        }


    }


    @Override
    public void close() throws Exception {
        super.close();
    }
}
