package com.littleyes.agent.core.jvm.memory;

import com.littleyes.common.dto.jvm.MemoryPoolMetric;

import java.util.List;

/**
 * <p> <b> MemoryPoolMetric 指标数据收集接口 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-26
 */
public interface MemoryPoolMetricAccessor {

    List<MemoryPoolMetric> getMemoryPoolMetricList();

}
