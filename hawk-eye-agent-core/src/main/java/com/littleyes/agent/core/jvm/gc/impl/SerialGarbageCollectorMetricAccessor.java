package com.littleyes.agent.core.jvm.gc.impl;

import com.littleyes.agent.core.jvm.gc.BaseGarbageCollectorMetricAccessor;

import java.lang.management.GarbageCollectorMXBean;
import java.util.List;

/**
 * <p> <b> Serial GarbageCollectorMetric 指标数据收集实现 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-25
 */
public final class SerialGarbageCollectorMetricAccessor extends BaseGarbageCollectorMetricAccessor {

    public static final String SERIAL_MARKER = "MarkSweepCompact";

    public SerialGarbageCollectorMetricAccessor(List<GarbageCollectorMXBean> gcMxBeans) {
        super(gcMxBeans);
    }

    @Override
    protected String getMinorGarbageCollectorName() {
        return "Copy";
    }

    @Override
    protected String getMajorGarbageCollectorName() {
        return SERIAL_MARKER;
    }

}
