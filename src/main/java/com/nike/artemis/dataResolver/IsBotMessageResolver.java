package com.nike.artemis.dataResolver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nike.artemis.LogMsgBuilder;
import com.nike.artemis.model.block.Block;
import com.nike.artemis.model.launch.IsBotData;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

public class IsBotMessageResolver implements FlatMapFunction<String, Block> {
    public static Logger LOG = LoggerFactory.getLogger(IsBotMessageResolver.class);
    ObjectMapper mapper = new ObjectMapper();

    @Override
    public void flatMap(String eventData, Collector<Block> out) {
        if (eventData.isEmpty()) return;

        LOG.debug(LogMsgBuilder.getInstance()
                .source(IsBotMessageResolver.class.getSimpleName())
                .msg(String.format("Isbot result data before extraction: %s, current_time: %s",
                        eventData, LocalDateTime.now().toInstant(ZoneOffset.ofHours(0)).toEpochMilli())).toString());

        try {
            IsBotData isBotData = mapper.readValue(eventData, IsBotData.class);
            if (!isBotData.getValidationSummary().isEmpty() && Objects.equals(isBotData.getValidationSummary().get("result"), "INVALID")) {
                out.collect(new Block("AT-ISBOT-1", "upmid", isBotData.getUpmId(), "catch", "90", "edgeKV-batch", "testIsbot", "90"));
            }
        } catch (JsonProcessingException e) {
            LOG.error(LogMsgBuilder.getInstance()
                    .source(IsBotMessageResolver.class.getSimpleName())
                    .msg("unmarshalling Isbot result data failed")
                    .data(eventData)
                    .exception(e.getMessage()).toString());
        }
    }
}
