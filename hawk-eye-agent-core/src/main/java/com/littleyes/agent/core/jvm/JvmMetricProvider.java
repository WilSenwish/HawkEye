package com.littleyes.agent.core.jvm;

import com.littleyes.agent.core.jvm.gc.GarbageCollectorMetricProvider;
import com.littleyes.agent.core.jvm.memory.MemoryMetricProvider;
import com.littleyes.agent.core.jvm.memory.MemoryPoolMetricProvider;
import com.littleyes.agent.core.jvm.thread.ThreadMetricProvider;
import com.littleyes.collector.dto.jvm.JvmMetric;

/**
 * <p> <b> JVM 指标数据收集入口 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-26
 */
public final class JvmMetricProvider {

    private JvmMetricProvider() {
    }

    public static JvmMetric getJvmMetric() {
        return JvmMetric.builder()
                .garbageCollectorMetrics(GarbageCollectorMetricProvider.getGarbageCollectorMetricList())
                .memoryMetrics(MemoryMetricProvider.getMemoryMetricList())
                .memoryPoolMetrics(MemoryPoolMetricProvider.getMemoryPoolMetricList())
                .threadMetric(ThreadMetricProvider.getThreadMetric())
                .build();
    }

}
