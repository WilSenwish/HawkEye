package com.littleyes.collector.dto.jvm;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

/**
 * <p> <b> Memory Pool Metric Bean </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-26
 */
@EqualsAndHashCode
@ToString
@Getter
@Builder
public class MemoryPoolMetric implements Serializable {

    private static final long serialVersionUID = 1L;

    private MemoryPoolType type;
    private long init;
    private long used;
    private long committed;
    private long max;

}
