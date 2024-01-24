package com.nike.artemis.processWindows;

import com.nike.artemis.LogMsgBuilder;
import com.nike.artemis.model.block.Block;
import com.nike.artemis.model.rules.LaunchRateRule;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LaunchRuleProcessWindowFunction extends ProcessWindowFunction<Long, Block, Tuple3<String, String, LaunchRateRule>, TimeWindow> {
    public static Logger LOG = LoggerFactory.getLogger(LaunchRuleProcessWindowFunction.class);

    @Override
    public void process(Tuple3<String, String, LaunchRateRule> stringRateRuleTuple2, ProcessWindowFunction<Long, Block,
            Tuple3<String, String, LaunchRateRule>, TimeWindow>.Context context, Iterable<Long> elements, Collector<Block> out) throws Exception {
        String blockEntity = stringRateRuleTuple2.f0;
        LaunchRateRule rateRule = stringRateRuleTuple2.f2;

        if ((elements == null) || (!elements.iterator().hasNext()))
            return;
        long count = elements.iterator().next();

        if (count == rateRule.getLimit()) {
            Block block = new Block(rateRule.getRuleId(), rateRule.getBlockKind().name(), blockEntity, rateRule.getAction(),
                    String.valueOf(context.window().getStart() + rateRule.getExpiration()), "dynamo", "", "");
            String logMsg;

            if (rateRule.isEnforce()) {
                out.collect(block);
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
                    .windowEnd(context.window().getEnd()).toString());
        }
    }

    @Override
    public void close() throws Exception {
        super.close();
    }
}
