package com.littleyes.agent.core.jvm.gc.impl;

import com.littleyes.agent.core.jvm.gc.BaseGarbageCollectorMetricAccessor;

import java.lang.management.GarbageCollectorMXBean;
import java.util.List;

/**
 * <p> <b> G1 GarbageCollectorMetric 指标数据收集实现 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-25
 */
public final class G1GarbageCollectorMetricAccessor extends BaseGarbageCollectorMetricAccessor {

    public static final String G1_MARKER = "G1";

    public G1GarbageCollectorMetricAccessor(List<GarbageCollectorMXBean> gcMxBeans) {
        super(gcMxBeans);
    }

    @Override
    protected String getMinorGarbageCollectorName() {
        return G1_MARKER + " Young Generation";
    }

    @Override
    protected String getMajorGarbageCollectorName() {
        return G1_MARKER + " Old Generation";
    }

}
