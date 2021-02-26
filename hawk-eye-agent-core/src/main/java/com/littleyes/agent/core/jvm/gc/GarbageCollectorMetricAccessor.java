package com.littleyes.agent.core.jvm.gc;

import com.littleyes.collector.dto.jvm.GarbageCollectorMetric;

import java.util.List;

/**
 * <p> <b> GarbageCollectorMetric 指标数据收集接口 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-25
 */
public interface GarbageCollectorMetricAccessor {

    List<GarbageCollectorMetric> getGarbageCollectorMetricList();

}
