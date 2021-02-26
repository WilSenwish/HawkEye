package com.littleyes.agent.core.jvm.gc.impl;

import com.littleyes.agent.core.jvm.gc.BaseGarbageCollectorMetricAccessor;

import java.lang.management.GarbageCollectorMXBean;
import java.util.List;

/**
 * <p> <b> Parallel GarbageCollectorMetric 指标数据收集实现 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-25
 */
public final class ParallelGarbageCollectorMetricAccessor extends BaseGarbageCollectorMetricAccessor {

    public static final String PARALLEL_GC_MARKER = "PS";

    public ParallelGarbageCollectorMetricAccessor(List<GarbageCollectorMXBean> gcMxBeans) {
        super(gcMxBeans);
    }

    @Override
    protected String getMinorGarbageCollectorName() {
        return PARALLEL_GC_MARKER + " Scavenge";
    }

    @Override
    protected String getMajorGarbageCollectorName() {
        return PARALLEL_GC_MARKER + " MarkSweep";
    }

}
