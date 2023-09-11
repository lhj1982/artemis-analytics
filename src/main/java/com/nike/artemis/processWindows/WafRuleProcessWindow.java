package com.nike.artemis.processWindows;

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

public class WafRuleProcessWindow extends ProcessWindowFunction<Long, Block, Tuple2<String, WafRateRule>, TimeWindow> {

    static Logger LOG = LoggerFactory.getLogger(WafRuleProcessWindow.class);
    ValueStateDescriptor<Long> currentWafMaxBlockByUserAndRule;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        currentWafMaxBlockByUserAndRule = new ValueStateDescriptor<>("wafMaxBlockByUserAndRule", Long.class);
    }

    @Override
    public void process(Tuple2<String, WafRateRule> key, ProcessWindowFunction<Long, Block, Tuple2<String, WafRateRule>, TimeWindow>.Context context, Iterable<Long> elements, Collector<Block> out) throws Exception {
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
        LOG.info("in the processWindow WAF: request user: {} window start: {} window endtime: {}",user,  context.window().getStart(),context.window().getEnd());
        if (count >= wafRateRule.getLimit()) {
           Long newBlockEnd = context.window().getStart() + wafRateRule.getBlock_time();
           if (currentMaxBlock < newBlockEnd) {
               LOG.info("EMIT WAF BLOCK: rule name: {}, user type: {}, user: {}, blockttl: {}", wafRateRule.getRule_name(), wafRateRule.getUser_type(), user, newBlockEnd);
               out.collect(new Block(wafRateRule.getRule_name(), wafRateRule.getUser_type(), user, "block", String.valueOf(newBlockEnd), "edgeKV", wafRateRule.getName_space()));
               maxBlockState.update(newBlockEnd);
           }
        }
    }

    @Override
    public void close() throws Exception {
        super.close();
    }

}
