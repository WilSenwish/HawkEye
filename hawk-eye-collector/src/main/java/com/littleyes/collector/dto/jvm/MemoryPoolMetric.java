package com.littleyes.collector.dto.jvm;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * <p> <b> Memory Pool Metric Bean </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-26
 */
@ToString
@Getter
@Builder
public class MemoryPoolMetric {

    private MemoryPoolType type;
    private long init;
    private long used;
    private long committed;
    private long max;

}
