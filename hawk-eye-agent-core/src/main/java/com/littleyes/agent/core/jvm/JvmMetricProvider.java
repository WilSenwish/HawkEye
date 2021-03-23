package com.littleyes.agent.core.jvm;

import com.littleyes.agent.core.jvm.gc.GarbageCollectorMetricProvider;
import com.littleyes.agent.core.jvm.memory.MemoryMetricProvider;
import com.littleyes.agent.core.jvm.memory.MemoryPoolMetricProvider;
import com.littleyes.agent.core.jvm.thread.ThreadMetricProvider;
import com.littleyes.collector.buf.JvmMetricBuffer;
import com.littleyes.common.dto.jvm.JvmMetric;
import com.littleyes.threadpool.core.HawkEyeThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.littleyes.agent.core.util.Constants.HAWK_EYE_AGENT;
import static org.apache.commons.lang3.time.DateUtils.MILLIS_PER_MINUTE;

/**
 * <p> <b> JVM 指标数据收集入口 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-26
 */
@Slf4j
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

    public static void monitorJvm() {
        String samplerName = "JvmMetricSampler";
        long period = MILLIS_PER_MINUTE / 6;

        Executors
                .newSingleThreadScheduledExecutor(new HawkEyeThreadFactory(samplerName, true))
                .scheduleAtFixedRate(
                        () -> JvmMetricBuffer.offer(getJvmMetric()),
                        MILLIS_PER_MINUTE,
                        period,
                        TimeUnit.MILLISECONDS
                );

        log.info("{} Initialized [{}]!", HAWK_EYE_AGENT, samplerName);
    }

}
