package com.nike.artemis.processWindows;

import com.nike.artemis.LogMsgBuilder;
import com.nike.artemis.model.block.Block;
import com.nike.artemis.model.rules.WafRateRule;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class WafRuleUmidProcessWindowFunction extends ProcessWindowFunction<LinkedHashSet<String>, Block, Tuple2<String, WafRateRule>, TimeWindow> {
    static Logger LOG = LoggerFactory.getLogger(WafRuleUmidProcessWindowFunction.class);
    private ValueState<Long> userCount;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        userCount = getRuntimeContext().getState(new ValueStateDescriptor<>("wafUserWithSameDevice", Long.class));
    }

    @Override
    public void process(Tuple2<String, WafRateRule> key, ProcessWindowFunction<LinkedHashSet<String>, Block, Tuple2<String, WafRateRule>, TimeWindow>.Context context,
                        Iterable<LinkedHashSet<String>> elements, Collector<Block> out) throws Exception {
        String deviceId = key.f0;
        WafRateRule wafRateRule = key.f1;

        if ((elements == null) || (!elements.iterator().hasNext()))
            return;
        long count = elements.iterator().next().size();

        LOG.debug(LogMsgBuilder.getInstance()
                .source(WafRateRule.class.getSimpleName())
                .msg(String.format("in the processWindow WAF: deviceId: %s, request users: %s, window start at: %s, window end at: %s",
                        deviceId, elements.iterator().next(), context.window().getStart(), context.window().getEnd())).toString());
        if (userCount.value() == null) {
            userCount.update(0L);
        }
        if (count >= wafRateRule.getLimit()) {
            if (userCount.value() == 0) {
                // send the threshold limit actors and send into the following operator or KDS, since we need to block all the upmid with the same device
                elements.iterator().next().forEach(upmid -> sendToNextOperator(upmid, wafRateRule, context, out));
                userCount.update(wafRateRule.getLimit());
            } else {
                if (count > userCount.value()) {
                    // send the (count)th element to the next operator
                    ArrayList<String> upmids = new ArrayList<>(elements.iterator().next());
                    String upmid = upmids.get((int) count - 1);
                    sendToNextOperator(upmid, wafRateRule, context, out);
                    userCount.update(count);
                }
            }
        }
    }

    private void sendToNextOperator(String user, WafRateRule wafRateRule, Context context, Collector<Block> out) {
        long blockTime = context.currentWatermark();
        Block block = new Block(wafRateRule.getRule_id(), wafRateRule.getUser_type(), user, wafRateRule.getAction(),
                String.valueOf(context.window().getStart() + wafRateRule.getBlock_time()), "edgeKV",
                wafRateRule.getName_space(), String.valueOf(wafRateRule.getTtl()), blockTime);
        String logMsg;

        if (wafRateRule.isEnforce()) {
            out.collect(block);
            logMsg = "EMIT WAF BLOCK";
        } else {
            logMsg = "WAF Rule EnforceType: NO";
        }
        LOG.info(LogMsgBuilder.getInstance()
                .source(WafRateRule.class.getSimpleName())
                .msg(logMsg)
                .block(block)
                .ruleName(wafRateRule.getRule_name())
                .path(wafRateRule.getPath())
                .blockTime(blockTime)
                .windowStart(context.window().getStart())
                .windowEnd(context.window().getEnd()).toString());
    }


}
