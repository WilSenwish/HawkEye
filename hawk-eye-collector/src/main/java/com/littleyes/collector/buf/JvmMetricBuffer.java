package com.littleyes.collector.buf;

import com.littleyes.common.dto.jvm.JvmMetric;
import com.littleyes.collector.worker.HawkEyeJvmMetricCollector;
import com.littleyes.common.config.HawkEyeConfig;
import com.littleyes.common.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static com.littleyes.collector.util.Constants.BUFFER_MAX_CAPACITY;
import static com.littleyes.collector.util.Constants.HAWK_EYE_COLLECTOR;

/**
 * <p> <b> JvmMetric 缓存 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-03-16
 */
@Slf4j
public class JvmMetricBuffer {

    private static final BlockingQueue<JvmMetric> BUFFER = new ArrayBlockingQueue<>(BUFFER_MAX_CAPACITY >> 6);
    private static HawkEyeJvmMetricCollector hawkEyeJvmMetricCollector;

    static {
        initialize();
    }

    private JvmMetricBuffer() {
    }

    private static void initialize() {
        if (HawkEyeConfig.isPerformanceDisabled()) {
            log.info("{} Performance monitor disabled!!!", HAWK_EYE_COLLECTOR);
            return;
        }

        if (Objects.nonNull(hawkEyeJvmMetricCollector)) {
            log.info("{} {} already started!!!", HAWK_EYE_COLLECTOR, hawkEyeJvmMetricCollector.getName());
            return;
        }

        hawkEyeJvmMetricCollector = new HawkEyeJvmMetricCollector(BUFFER);
        hawkEyeJvmMetricCollector.start();
    }

    public static void offer(JvmMetric jvmMetric) {
        if (HawkEyeConfig.isPerformanceDisabled()) {
            log.info("{} Performance monitor disabled!!!", HAWK_EYE_COLLECTOR);
            return;
        }

        if (Objects.isNull(hawkEyeJvmMetricCollector) || !hawkEyeJvmMetricCollector.isAlive()) {
            log.info("{} {} not started or died!!!", HAWK_EYE_COLLECTOR, hawkEyeJvmMetricCollector.getName());
            return;
        }

        try {
            boolean success = BUFFER.offer(jvmMetric);
            if (!success) {
                log.warn("{} Jvm Metric [{}] queue failed!!!", HAWK_EYE_COLLECTOR, JsonUtils.toString(jvmMetric));
            }
        } catch (Exception ignore) {
        }
    }

}
