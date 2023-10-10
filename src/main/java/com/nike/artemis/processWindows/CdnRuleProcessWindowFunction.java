package com.nike.artemis.processWindows;

import com.nike.artemis.LogMsgBuilder;
import com.nike.artemis.model.block.Block;
import com.nike.artemis.model.rules.CdnRateRule;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class CdnRuleProcessWindowFunction extends ProcessWindowFunction<Long, Block, Tuple2<String, CdnRateRule>, TimeWindow> {
    public static Logger LOG = LoggerFactory.getLogger(CdnRuleProcessWindowFunction.class);
    ValueStateDescriptor<Long> currentCdnMaxBlockByUserAndRule;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        currentCdnMaxBlockByUserAndRule = new ValueStateDescriptor<>("cdnMaxBlockByUserAndRule", Long.class);
    }

    @Override
    public void process(Tuple2<String, CdnRateRule> key, ProcessWindowFunction<Long, Block, Tuple2<String, CdnRateRule>,
            TimeWindow>.Context context, Iterable<Long> elements, Collector<Block> out) throws Exception {
        String user = key.f0;
        CdnRateRule cdnRateRule = key.f1;

        if ((elements == null) || (!elements.iterator().hasNext()))
            return;
        long count = elements.iterator().next();

        ValueState<Long> maxBlockState = context.globalState().getState(currentCdnMaxBlockByUserAndRule);
        if (maxBlockState.value() == null) {
            context.globalState().getState(currentCdnMaxBlockByUserAndRule).update(0L);
        }
        long currentMaxBlock = maxBlockState.value();
        LOG.debug(LogMsgBuilder.getInstance()
                .source(CdnRateRule.class.getSimpleName())
                .msg(String.format("Processing CDN data timeStamp :%s", LocalDateTime.now().toInstant(ZoneOffset.ofHours(0)).toEpochMilli()))
                .build().toString());
        LOG.debug(LogMsgBuilder.getInstance()
                .source(CdnRateRule.class.getSimpleName())
                .msg(String.format("in the processWindow CDN: request user: %s, window start at: %s, window end at: %s",
                        user, context.window().getStart(), context.window().getEnd()))
                .build().toString());
        if (count >= cdnRateRule.getLimit()) {
            long newBlockEnd = context.window().getStart() + cdnRateRule.getBlock_time();
            if ((currentMaxBlock < newBlockEnd)) {
                LOG.info(LogMsgBuilder.getInstance()
                        .source(CdnRateRule.class.getSimpleName())
                        .msg(String.format("EMIT CDN BLOCK: rule name: %s, user type: %s, user: %s, block ttl: %s",
                                cdnRateRule.getRule_name(), cdnRateRule.getUser_type(), user, newBlockEnd))
                        .build().toString());
                out.collect(new Block(cdnRateRule.getRule_id(), cdnRateRule.getUser_type(), user, cdnRateRule.getAction(),
                        String.valueOf(newBlockEnd), "edgeKV", cdnRateRule.getName_space(), String.valueOf(cdnRateRule.getTtl())));
                maxBlockState.update(newBlockEnd);
            }
        }
    }

    @Override
    public void close() throws Exception {
        super.close();
    }
}
