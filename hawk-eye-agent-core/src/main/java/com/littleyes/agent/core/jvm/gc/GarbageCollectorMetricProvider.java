package com.littleyes.agent.core.jvm.gc;

import com.littleyes.agent.core.jvm.gc.impl.*;
import com.littleyes.collector.dto.jvm.GarbageCollectorMetric;
import lombok.extern.slf4j.Slf4j;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Objects;

import static com.littleyes.agent.core.jvm.gc.impl.CMSGarbageCollectorMetricAccessor.CMS_MARKER;
import static com.littleyes.agent.core.jvm.gc.impl.G1GarbageCollectorMetricAccessor.G1_MARKER;
import static com.littleyes.agent.core.jvm.gc.impl.ParallelGarbageCollectorMetricAccessor.PARALLEL_MARKER;
import static com.littleyes.agent.core.jvm.gc.impl.SerialGarbageCollectorMetricAccessor.SERIAL_MARKER;
import static com.littleyes.agent.core.util.Constants.HAWK_EYE_AGENT;

/**
 * <p> <b> GarbageCollectorMetric 指标数据收集入口 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-25
 */
@Slf4j
public final class GarbageCollectorMetricProvider {

    private static GarbageCollectorMetricAccessor metricAccessor;
    private static List<GarbageCollectorMXBean> gcMxBeans;

    static {
        gcMxBeans = ManagementFactory.getGarbageCollectorMXBeans();
        for (GarbageCollectorMXBean bean : gcMxBeans) {
            String name = bean.getName();
            log.info("{} Current GarbageCollectorMXBean [{}]", HAWK_EYE_AGENT, bean.getName());

            GarbageCollectorMetricAccessor accessor = findByBeanName(name);
            if (Objects.isNull(metricAccessor) && Objects.nonNull(accessor)) {
                metricAccessor = accessor;
            }
        }

        if (Objects.isNull(metricAccessor)) {
            metricAccessor = new UnknownGarbageCollectorMetricAccessor();
        }
        log.info("{} Current GarbageCollectorMetricAccessor [{}]", HAWK_EYE_AGENT, metricAccessor);
    }

    public static List<GarbageCollectorMetric> getGarbageCollectorMetricList() {
        return metricAccessor.getGarbageCollectorMetricList();
    }

    private static GarbageCollectorMetricAccessor findByBeanName(String name) {
        if (name.contains(CMS_MARKER)) {
            // CMS collector ( -XX:+UseConcMarkSweepGC )
            return new CMSGarbageCollectorMetricAccessor(gcMxBeans);
        } else if (name.contains(G1_MARKER)) {
            // G1 collector ( -XX:+UseG1GC )
            return new G1GarbageCollectorMetricAccessor(gcMxBeans);
        } else if (name.contains(PARALLEL_MARKER)) {
            //Parallel (Old) collector ( -XX:+UseParallelOldGC )
            return new ParallelGarbageCollectorMetricAccessor(gcMxBeans);
        } else if (name.contains(SERIAL_MARKER)) {
            // Serial collector ( -XX:+UseSerialGC )
            return new SerialGarbageCollectorMetricAccessor(gcMxBeans);
        } else {
            // Unknown
            return null;
        }
    }

}
