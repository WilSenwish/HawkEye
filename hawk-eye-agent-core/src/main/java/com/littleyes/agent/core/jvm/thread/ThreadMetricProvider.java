package com.littleyes.agent.core.jvm.thread;

import com.littleyes.collector.dto.jvm.ThreadMetric;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

/**
 * <p> <b> 线程指标数据收集入口 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-26
 */
public final class ThreadMetricProvider {

    private static ThreadMXBean threadMXBean;

    static {
        threadMXBean = ManagementFactory.getThreadMXBean();
    }

    private ThreadMetricProvider() {
    }

    public static ThreadMetric getThreadMetric() {
        int liveThreadCount = threadMXBean.getThreadCount();
        int peakThreadCount = threadMXBean.getPeakThreadCount();
        int daemonThreadCount = threadMXBean.getDaemonThreadCount();

        return ThreadMetric.builder()
                .liveThreadCount(liveThreadCount)
                .peakThreadCount(peakThreadCount)
                .daemonThreadCount(daemonThreadCount)
                .build();
    }

}
