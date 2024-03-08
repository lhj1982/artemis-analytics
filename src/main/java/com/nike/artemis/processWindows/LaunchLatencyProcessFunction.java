package com.nike.artemis.processWindows;

import com.nike.artemis.model.Latency;
import com.nike.artemis.model.launch.LaunchRequestEvent;
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

public class LaunchLatencyProcessFunction extends KeyedProcessFunction<String, LaunchRequestEvent, Latency> {

    private transient ValueState<LaunchRequestEvent> launchData;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        launchData = getRuntimeContext().getState(new ValueStateDescriptor<>("launch-request",
                TypeInformation.of(LaunchRequestEvent.class)));
    }

    @Override
    public void processElement(LaunchRequestEvent launchRequestEvent,
                               KeyedProcessFunction<String, LaunchRequestEvent, Latency>.Context context,
                               Collector<Latency> collector) throws Exception {
        LaunchRequestEvent requestEvent = launchData.value();
        if (Objects.isNull(requestEvent)) {
            launchData.update(launchRequestEvent);
            context.timerService().registerProcessingTimeTimer(System.currentTimeMillis() + 1000);
        }
    }

    @Override
    public void onTimer(long timestamp, KeyedProcessFunction<String, LaunchRequestEvent, Latency>.OnTimerContext ctx,
                        Collector<Latency> out) throws Exception {
        long currentTime = LocalDateTime.now().toInstant(ZoneOffset.ofHours(0)).toEpochMilli();
        LaunchRequestEvent requestEvent = launchData.value();
        // publish to sns time(@timestamp in the request) - artemis analytics time = artemis latency
        out.collect(Latency.builder()
                .latency((double) (currentTime - requestEvent.getTimestamp()))
                .timestamp(Instant.ofEpochMilli(requestEvent.getTimestamp()))
                .type("launch_artemis_latency")
                .build());
    }
}
