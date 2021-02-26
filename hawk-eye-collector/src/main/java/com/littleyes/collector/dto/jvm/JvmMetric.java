package com.littleyes.collector.dto.jvm;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * <p> <b> JVM Metric Bean </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-26
 */
@ToString
@Getter
@Builder
public class JvmMetric implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<GarbageCollectorMetric> garbageCollectorMetrics;
    private List<MemoryMetric> memoryMetrics;
    private List<MemoryPoolMetric> memoryPoolMetrics;
    private ThreadMetric threadMetric;

}
