package com.nike.artemis.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;

@Getter
@Builder
@ToString
public class Latency {
    private final double latency;
    private final Instant timestamp;
    private final String type;

}
