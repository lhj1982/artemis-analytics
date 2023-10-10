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

public class WafRuleProcessWindowFunction extends ProcessWindowFunction<Long, Block, Tuple2<String, WafRateRule>, TimeWindow> {

    static Logger LOG = LoggerFactory.getLogger(WafRuleProcessWindowFunction.class);
    ValueStateDescriptor<Long> currentWafMaxBlockByUserAndRule;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        currentWafMaxBlockByUserAndRule = new ValueStateDescriptor<>("wafMaxBlockByUserAndRule", Long.class);
    }

    @Override
    public void process(Tuple2<String, WafRateRule> key, ProcessWindowFunction<Long, Block, Tuple2<String, WafRateRule>,
            TimeWindow>.Context context, Iterable<Long> elements, Collector<Block> out) throws Exception {
        String user = key.f0;
        WafRateRule wafRateRule = key.f1;

        if ((elements == null) || (!elements.iterator().hasNext()))
            return;
        long count = elements.iterator().next();

        ValueState<Long> maxBlockState = context.globalState().getState(currentWafMaxBlockByUserAndRule);
        if (maxBlockState.value() == null) {
            context.globalState().getState(currentWafMaxBlockByUserAndRule).update(0L);
        }

        long currentMaxBlock = maxBlockState.value();
        LOG.debug(LogMsgBuilder.getInstance()
                .source(WafRateRule.class.getSimpleName())
                .msg(String.format("in the processWindow WAF: request user: %s, window start at: %s, window end at: %s",
                        user, context.window().getStart(), context.window().getEnd()))
                .build().toString());
        if (count >= wafRateRule.getLimit()) {
            Long newBlockEnd = context.window().getStart() + wafRateRule.getBlock_time();
            if (currentMaxBlock < newBlockEnd) {
                LOG.info(LogMsgBuilder.getInstance()
                        .source(WafRateRule.class.getSimpleName())
                        .msg(String.format("EMIT WAF BLOCK: rule name: %s, user type: %s, user: %s, block ttl: %s",
                                wafRateRule.getRule_name(), wafRateRule.getUser_type(), user, newBlockEnd))
                        .build().toString());
                out.collect(new Block(wafRateRule.getRule_id(), wafRateRule.getUser_type(), user, wafRateRule.getAction(),
                        String.valueOf(newBlockEnd), "edgeKV", wafRateRule.getName_space(), String.valueOf(wafRateRule.getTtl())));
                maxBlockState.update(newBlockEnd);
            }
        }
    }

    @Override
    public void close() throws Exception {
        super.close();
    }

}
