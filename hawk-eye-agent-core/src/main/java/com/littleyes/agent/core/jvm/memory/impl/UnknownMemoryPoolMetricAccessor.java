package com.littleyes.agent.core.jvm.memory.impl;

import com.littleyes.agent.core.jvm.memory.MemoryPoolMetricAccessor;
import com.littleyes.collector.dto.jvm.MemoryPoolMetric;
import com.littleyes.collector.dto.jvm.MemoryPoolType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p> <b> Unknown MemoryPoolMetricAccessor 指标数据收集实现 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-26
 */
public class UnknownMemoryPoolMetricAccessor implements MemoryPoolMetricAccessor {

    @Override
    public List<MemoryPoolMetric> getMemoryPoolMetricList() {
        return Arrays.stream(MemoryPoolType.values())
                .map(t -> MemoryPoolMetric.builder().type(t).build())
                .collect(Collectors.toList());
    }

}
