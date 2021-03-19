package com.littleyes.collector.dto.jvm;

import com.littleyes.collector.dto.BaseDto;
import lombok.Builder;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode
@ToString
@Getter
@Builder
public class JvmMetric extends BaseDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<GarbageCollectorMetric> garbageCollectorMetrics;
    private List<MemoryMetric> memoryMetrics;
    private List<MemoryPoolMetric> memoryPoolMetrics;
    private ThreadMetric threadMetric;

}
