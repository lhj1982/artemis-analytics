package com.nike.artemis.processWindows;

import com.nike.artemis.model.Latency;
import com.nike.artemis.model.cdn.CdnRequestEvent;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

public class CdnLatencyProcessFunction extends KeyedProcessFunction<Long, CdnRequestEvent, Latency> {
    private transient ValueState<CdnRequestEvent> cdnData;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        cdnData = getRuntimeContext().getState(new ValueStateDescriptor<>("cdn-request",
                TypeInformation.of(CdnRequestEvent.class)));
    }

    @Override
    public void processElement(CdnRequestEvent cdnRequestEvent,
                               KeyedProcessFunction<Long, CdnRequestEvent, Latency>.Context context,
                               Collector<Latency> collector) throws Exception {

        CdnRequestEvent requestEvent = cdnData.value();
        if (Objects.isNull(requestEvent)) {
            // use System.currentTimeMillis() since cloudwatch metrics PUT time are in system time. CDN time is in milliseconds.
            long timeDifference = (LocalDateTime.now().toInstant(ZoneOffset.ofHours(0)).toEpochMilli() - cdnRequestEvent.getTime())/ (1000 * 60);
            // request time should be not older than 1 hour and not more than 30 minutes in the future, durations are picked based on suitable values.
            if (timeDifference <= 60 && timeDifference >= -30) {
                cdnData.update(cdnRequestEvent);
                context.timerService().registerProcessingTimeTimer(System.currentTimeMillis() + 1000);
            }
        }
    }

    @Override
    public void onTimer(long timestamp, KeyedProcessFunction<Long, CdnRequestEvent, Latency>.OnTimerContext ctx,
                        Collector<Latency> out) throws Exception {
        CdnRequestEvent requestEvent = cdnData.value();
        long currentTime = LocalDateTime.now().toInstant(ZoneOffset.ofHours(0)).toEpochMilli();
        long userRequestTime = requestEvent.getTime();
        long cdnSlsTime = requestEvent.getSlsTime();

        // user request time - cdn sls time = ali latency
        out.collect(Latency.builder()
                .latency((double) (cdnSlsTime - userRequestTime))
                .timestamp(Instant.ofEpochMilli(userRequestTime))
                .type("cdn_ali_latency")
                .build());

        // cdn sls time - artemis analytics time = artemis latency
        out.collect(Latency.builder()
                .latency((double) (currentTime - cdnSlsTime))
                .timestamp(Instant.ofEpochMilli(userRequestTime))
                .type("cdn_artemis_latency")
                .build());

        // user request time - artemis analytics time = overall latency
        out.collect(Latency.builder()
                .latency((double) (currentTime - userRequestTime))
                .timestamp(Instant.ofEpochMilli(userRequestTime))
                .type("cdn_overall_latency")
                .build());
        cdnData.clear();
    }
}
