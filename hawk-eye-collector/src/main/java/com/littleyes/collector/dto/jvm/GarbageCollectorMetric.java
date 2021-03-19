package com.littleyes.collector.dto.jvm;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

/**
 * <p> <b> GarbageCollectorMetric Bean </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-25
 */
@EqualsAndHashCode
@ToString
@Getter
@Builder
public class GarbageCollectorMetric implements Serializable {

    private static final long serialVersionUID = 1L;

    private GarbageCollectorPhrase phrase;
    private long count;
    private long time;

}
