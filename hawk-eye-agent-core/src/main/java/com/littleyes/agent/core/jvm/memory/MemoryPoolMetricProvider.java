package com.littleyes.agent.core.jvm.memory;

import com.littleyes.agent.core.jvm.memory.impl.*;
import com.littleyes.common.dto.jvm.MemoryPoolMetric;
import lombok.extern.slf4j.Slf4j;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.util.List;
import java.util.Objects;

import static com.littleyes.agent.core.jvm.memory.impl.CMSMemoryPoolMetricAccessor.CMS_MP_MARKER;
import static com.littleyes.agent.core.jvm.memory.impl.G1MemoryPoolMetricAccessor.G1_MP_MARKER;
import static com.littleyes.agent.core.jvm.memory.impl.ParallelMemoryPoolMetricAccessor.PARALLEL_MP_MARKER;
import static com.littleyes.agent.core.jvm.memory.impl.SerialMemoryPoolMetricAccessor.SERIAL_MP_MARKER;
import static com.littleyes.agent.core.util.Constants.HAWK_EYE_AGENT;

/**
 * <p> <b> Memory Pool 指标数据收集入口 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-26
 */
@Slf4j
public final class MemoryPoolMetricProvider {

    private static MemoryPoolMetricAccessor metricAccessor;
    private static List<MemoryPoolMXBean> beans;

    static {
        beans = ManagementFactory.getMemoryPoolMXBeans();

        for (MemoryPoolMXBean bean : beans) {
            String name = bean.getName();
            log.info("{} Current MemoryPoolMXBean [{}]", HAWK_EYE_AGENT, bean.getName());

            MemoryPoolMetricAccessor accessor = findByBeanName(name);
            if (Objects.isNull(metricAccessor) && Objects.nonNull(accessor)) {
                metricAccessor = accessor;
            }
        }

        if (metricAccessor == null) {
            metricAccessor = new UnknownMemoryPoolMetricAccessor();
        }

        log.info("{} Current MemoryPoolMetricProvider [{}]", HAWK_EYE_AGENT, metricAccessor);
    }

    private MemoryPoolMetricProvider() {
    }

    public static List<MemoryPoolMetric> getMemoryPoolMetricList() {
        return metricAccessor.getMemoryPoolMetricList();
    }

    private static MemoryPoolMetricAccessor findByBeanName(String name) {
        if (name.contains(CMS_MP_MARKER)) {
            // CMS collector ( -XX:+UseConcMarkSweepGC )
            return new CMSMemoryPoolMetricAccessor(beans);
        } else if (name.contains(G1_MP_MARKER)) {
            // G1 collector ( -XX:+UseG1GC )
            return new G1MemoryPoolMetricAccessor(beans);
        } else if (name.contains(PARALLEL_MP_MARKER)) {
            //Parallel (Old) collector ( -XX:+UseParallelOldGC )
            return new ParallelMemoryPoolMetricAccessor(beans);
        } else if (name.contains(SERIAL_MP_MARKER)) {
            // Serial collector ( -XX:+UseSerialGC )
            return new SerialMemoryPoolMetricAccessor(beans);
        } else {
            // Unknown
            return null;
        }
    }

}
