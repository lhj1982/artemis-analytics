package com.nike.artemis.processWindows;

import com.nike.artemis.LogMsgBuilder;
import com.nike.artemis.model.block.Block;
import com.nike.artemis.model.rules.LaunchRateRule;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class LaunchRuleProcessWindowFunction extends ProcessWindowFunction<Long, Block, Tuple3<String, String, LaunchRateRule>, TimeWindow> {
    public static Logger LOG = LoggerFactory.getLogger(LaunchRuleProcessWindowFunction.class);
    ValueStateDescriptor<Long> currentMaxBlockDescriptor;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        currentMaxBlockDescriptor = new ValueStateDescriptor<>("maxBlockByBlockKindAndRule", Long.class);
    }

    @Override
    public void process(Tuple3<String, String, LaunchRateRule> stringRateRuleTuple2, ProcessWindowFunction<Long, Block,
            Tuple3<String, String, LaunchRateRule>, TimeWindow>.Context context, Iterable<Long> elements, Collector<Block> out) throws Exception {
        String blockEntity = stringRateRuleTuple2.f0;
        LaunchRateRule rateRule = stringRateRuleTuple2.f2;

//        LOG.info("window assigned: block entity: {} current watermark: {} window start: {} window end: {}", blockEntity, new Timestamp(context.currentWatermark()), new Timestamp(context.window().getStart()), new Timestamp(context.window().getEnd()));
//        System.out.println(">>>>>>>>>>>>>>>"+blockEntity+" current water mark"+new Timestamp(context.currentWatermark())+"   windows:"+ context.window()+"    start:"+new Timestamp(context.window().getStart())+"   end:"+new Timestamp(context.window().getEnd()));
        if ((elements == null) || (!elements.iterator().hasNext()))
            return;
        Long count = elements.iterator().next();

        ValueState<Long> maxBlockState = context.globalState().getState(currentMaxBlockDescriptor);
        if (maxBlockState.value() == null) {
            context.globalState().getState(currentMaxBlockDescriptor).update(0L);
        }

        long currentMaxBlock = maxBlockState.value();

        if (count >= rateRule.getLimit()) {
            long newBlockEnd = context.window().getStart() + rateRule.getExpiration();
            if ((currentMaxBlock < newBlockEnd) && rateRule.isEnforce()) {
                Long startTime = LocalDateTime.now().toInstant(ZoneOffset.ofHours(0)).toEpochMilli();
                LOG.info(LogMsgBuilder.getInstance()
                        .source(LaunchRateRule.class.getSimpleName())
                        .msg(String.format("Block Generated: block kind: %s, block entity: %s, start time: %s, end time: %s, rule name: %s, timeStamp: %s",
                                rateRule.getBlockKind().name(), blockEntity, startTime, newBlockEnd, rateRule,
                                LocalDateTime.now().toInstant(ZoneOffset.ofHours(0)).toEpochMilli()))
                        .build().toString());
//                System.out.println("============[Generated a New Block:  "+new BlockEvent(rateRule.getBlockKind().name(), blockEntity,  LocalDateTime.now().toInstant(ZoneOffset.ofHours(8)).toEpochMilli(), newBlockEnd, rateRule.toString())+"]=========");
//                out.collect(new BlockEvent(rateRule.getBlockKind().name(), blockEntity,  startTime, newBlockEnd, rateRule.toString()));
                out.collect(new Block("Launch", rateRule.getBlockKind().name(), blockEntity, rateRule.getAction(),
                        String.valueOf(newBlockEnd), "dynamo", ""));
                maxBlockState.update(newBlockEnd);
            }
        }
    }

    @Override
    public void close() throws Exception {
        super.close();
    }
}
