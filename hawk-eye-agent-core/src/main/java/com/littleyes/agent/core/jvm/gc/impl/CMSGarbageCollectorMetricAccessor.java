package com.littleyes.agent.core.jvm.gc.impl;

import com.littleyes.agent.core.jvm.gc.BaseGarbageCollectorMetricAccessor;

import java.lang.management.GarbageCollectorMXBean;
import java.util.List;

/**
 * <p> <b> CMS GarbageCollectorMetric 指标数据收集实现 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-25
 */
public final class CMSGarbageCollectorMetricAccessor extends BaseGarbageCollectorMetricAccessor {

    public static final String CMS_GC_MARKER = "ConcurrentMarkSweep";

    public CMSGarbageCollectorMetricAccessor(List<GarbageCollectorMXBean> gcMxBeans) {
        super(gcMxBeans);
    }

    @Override
    protected String getMinorGarbageCollectorName() {
        return "ParNew";
    }

    @Override
    protected String getMajorGarbageCollectorName() {
        return CMS_GC_MARKER;
    }

}
