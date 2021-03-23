package com.littleyes.collector.buf;

import com.littleyes.common.dto.PerformanceLogDto;
import com.littleyes.collector.util.PerformanceContext;
import com.littleyes.collector.worker.HawkEyePerformanceCollector;
import com.littleyes.common.config.HawkEyeConfig;
import com.littleyes.common.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static com.littleyes.collector.util.Constants.BUFFER_MAX_CAPACITY;
import static com.littleyes.collector.util.Constants.HAWK_EYE_COLLECTOR;

/**
 * <p> <b> 性能日志缓存 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-22
 */
@Slf4j
public class PerformanceLogBuffer {

    private static final BlockingQueue<PerformanceLogDto> BUFFER = new ArrayBlockingQueue<>(BUFFER_MAX_CAPACITY);
    private static HawkEyePerformanceCollector hawkEyePerformanceCollector;

    static {
        new PerformanceLogBuffer();
    }

    private PerformanceLogBuffer() {
        if (HawkEyeConfig.isPerformanceDisabled()) {
            log.info("{} Performance monitor disabled!!!", HAWK_EYE_COLLECTOR);
            return;
        }

        if (Objects.nonNull(hawkEyePerformanceCollector)) {
            log.info("{} {} already started!!!", HAWK_EYE_COLLECTOR, hawkEyePerformanceCollector.getName());
            return;
        }

        hawkEyePerformanceCollector = new HawkEyePerformanceCollector(BUFFER);
        hawkEyePerformanceCollector.start();
    }

    public static void log(int type) {
        if (HawkEyeConfig.isPerformanceDisabled()) {
            log.info("{} Performance monitor disabled!!!", HAWK_EYE_COLLECTOR);
            return;
        }

        if (Objects.isNull(hawkEyePerformanceCollector) || !hawkEyePerformanceCollector.isAlive()) {
            log.info("{} {} not started or died!!!", HAWK_EYE_COLLECTOR, hawkEyePerformanceCollector.getName());
            return;
        }

        try {
            PerformanceLogDto performanceLog = PerformanceContext.buildPerformanceLog(type);
            boolean success = BUFFER.offer(performanceLog);
            if (!success) {
                log.warn("{} Performance log [{}] queue failed!!!", HAWK_EYE_COLLECTOR, JsonUtils.toString(performanceLog));
            }
        } catch (Exception ignore) {
        }
    }

}
