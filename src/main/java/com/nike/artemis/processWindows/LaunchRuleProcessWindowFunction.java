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
            Block block = new Block(rateRule.getRuleId(), rateRule.getBlockKind().name(), blockEntity, rateRule.getAction(),
                    String.valueOf(newBlockEnd), "dynamo", "", "");
            if (currentMaxBlock < newBlockEnd) {
                String logMsg;
                if (rateRule.isEnforce()) {
                    out.collect(block);
                    maxBlockState.update(newBlockEnd);
                    logMsg = "EMIT LAUNCH BLOCK";
                } else {
                    logMsg = "LAUNCH Rule EnforceType: NO";
                }
                LOG.info(LogMsgBuilder.getInstance()
                        .source(LaunchRateRule.class.getSimpleName())
                        .msg(logMsg)
                        .block(block)
                        .blockTime(context.currentWatermark())
                        .windowStart(context.window().getStart())
                        .windowEnd(context.window().getEnd())
                        .build().toString());
            }
        }
    }

    @Override
    public void close() throws Exception {
        super.close();
    }
}
