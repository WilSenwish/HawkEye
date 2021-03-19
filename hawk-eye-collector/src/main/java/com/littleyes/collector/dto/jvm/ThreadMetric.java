package com.littleyes.collector.dto.jvm;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

/**
 * <p> <b> ThreadMetric Bean </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-26
 */
@EqualsAndHashCode
@ToString
@Getter
@Builder
public class ThreadMetric implements Serializable {

    private static final long serialVersionUID = 1L;

    private int liveThreadCount;
    private int peakThreadCount;
    private int daemonThreadCount;

}
