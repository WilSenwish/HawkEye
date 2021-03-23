package com.littleyes.agent.core.jvm.gc.impl;

import com.littleyes.agent.core.jvm.gc.GarbageCollectorMetricAccessor;
import com.littleyes.common.dto.jvm.GarbageCollectorMetric;
import com.littleyes.common.dto.jvm.GarbageCollectorPhrase;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p> <b> Unknown GarbageCollectorMetric 指标数据收集实现 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-25
 */
public final class UnknownGarbageCollectorMetricAccessor implements GarbageCollectorMetricAccessor {

    @Override
    public List<GarbageCollectorMetric> getGarbageCollectorMetricList() {
        return Arrays.stream(GarbageCollectorPhrase.values())
                .map(p -> GarbageCollectorMetric.builder().phrase(p).build())
                .collect(Collectors.toList());
    }

}
