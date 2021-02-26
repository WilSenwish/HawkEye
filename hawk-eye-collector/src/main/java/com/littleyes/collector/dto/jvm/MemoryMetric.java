package com.littleyes.collector.dto.jvm;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * <p> <b> Memory Metric Bean </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-26
 */
@ToString
@Getter
@Builder
public class MemoryMetric {

    private MemoryArea area;
    private long init;
    private long used;
    private long committed;
    private long max;

}
