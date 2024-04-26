package com.nike.artemis.processWindows;

import com.nike.artemis.LogMsgBuilder;
import com.nike.artemis.model.block.Block;
import com.nike.artemis.model.rules.CdnRateRule;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class CdnRuleProcessWindowFunction extends ProcessWindowFunction<Long, Block, Tuple2<String, CdnRateRule>, TimeWindow> {
    public static Logger LOG = LoggerFactory.getLogger(CdnRuleProcessWindowFunction.class);

    @Override
    public void process(Tuple2<String, CdnRateRule> key, ProcessWindowFunction<Long, Block, Tuple2<String, CdnRateRule>,
            TimeWindow>.Context context, Iterable<Long> elements, Collector<Block> out) throws Exception {
        String user = key.f0;
        CdnRateRule cdnRateRule = key.f1;

        if ((elements == null) || (!elements.iterator().hasNext()))
            return;
        long count = elements.iterator().next();

        LOG.debug(LogMsgBuilder.getInstance()
                .source(CdnRateRule.class.getSimpleName())
                .msg(String.format("Processing CDN data timeStamp :%s", LocalDateTime.now().toInstant(ZoneOffset.ofHours(0)).toEpochMilli()))
                .toString());
        LOG.debug(LogMsgBuilder.getInstance()
                .source(CdnRateRule.class.getSimpleName())
                .msg(String.format("in the processWindow CDN: request user: %s, window start at: %s, window end at: %s",
                        user, context.window().getStart(), context.window().getEnd())).toString());

        if (count == cdnRateRule.getLimit()) {
            long blockTime = context.currentWatermark();
            Block block = new Block(cdnRateRule.getRule_id(), cdnRateRule.getUser_type(), user, cdnRateRule.getAction(),
                    String.valueOf(context.window().getStart() + cdnRateRule.getBlock_time()), "edgeKV",
                    cdnRateRule.getName_space(), String.valueOf(cdnRateRule.getTtl()), blockTime);
            String logMsg;

            if (cdnRateRule.isEnforce()) {
                out.collect(block);
                logMsg = "EMIT CDN BLOCK";
            } else {
                logMsg = "CDN Rule EnforceType: NO";
            }
            LOG.info(LogMsgBuilder.getInstance()
                    .source(CdnRateRule.class.getSimpleName())
                    .msg(logMsg)
                    .block(block)
                    .ruleName(cdnRateRule.getRule_name())
                    .path(cdnRateRule.getPath())
                    .blockTime(blockTime)
                    .windowStart(context.window().getStart())
                    .windowEnd(context.window().getEnd()).toString());
        }
    }

    @Override
    public void close() throws Exception {
        super.close();
    }
}
