package com.littleyes.agent.core.jvm.memory;

import com.littleyes.collector.dto.jvm.MemoryArea;
import com.littleyes.collector.dto.jvm.MemoryMetric;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.LinkedList;
import java.util.List;

/**
 * <p> <b> Memory 指标数据收集入口 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-26
 */
public final class MemoryMetricProvider {

    private static MemoryMXBean memoryMXBean;

    static {
        memoryMXBean = ManagementFactory.getMemoryMXBean();
    }

    private MemoryMetricProvider() {
    }

    public static List<MemoryMetric> getMemoryMetricList() {
        List<MemoryMetric> memoryMetricList = new LinkedList<>();

        MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
        memoryMetricList.add(
                MemoryMetric.builder()
                        .area(MemoryArea.HEAP)
                        .init(heapMemoryUsage.getInit())
                        .used(heapMemoryUsage.getUsed())
                        .committed(heapMemoryUsage.getCommitted())
                        .max(heapMemoryUsage.getMax())
                        .build()
        );

        MemoryUsage nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage();
        memoryMetricList.add(
                MemoryMetric.builder()
                        .area(MemoryArea.NON_HEAP)
                        .init(nonHeapMemoryUsage.getInit())
                        .used(nonHeapMemoryUsage.getUsed())
                        .committed(nonHeapMemoryUsage.getCommitted())
                        .max(nonHeapMemoryUsage.getMax())
                        .build()
        );

        return memoryMetricList;
    }

}
