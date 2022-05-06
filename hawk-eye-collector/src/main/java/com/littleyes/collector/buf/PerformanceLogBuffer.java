package com.littleyes.collector.buf;

import com.littleyes.collector.sample.HawkEyeSampleDecisionManager;
import com.littleyes.collector.worker.HawkEyePerformanceCollector;
import com.littleyes.common.config.HawkEyeConfig;
import com.littleyes.common.dto.PerformanceLogDto;
import com.littleyes.common.trace.TraceContext;
import com.littleyes.common.util.JsonUtils;
import com.littleyes.threadpool.util.HawkEyeExecutors;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

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

    private static ExecutorService sampler = HawkEyeExecutors.newThreadExecutor("Sampler", 16);

    private static final BlockingQueue<PerformanceLogDto> BUFFER = new ArrayBlockingQueue<>(BUFFER_MAX_CAPACITY);
    private static HawkEyePerformanceCollector hawkEyePerformanceCollector;

    static {
        initialize();
    }

    private PerformanceLogBuffer() {
    }

    private static void initialize() {
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

    public static void sample(PerformanceLogDto performanceLog) {
        if (!HawkEyeSampleDecisionManager.postDecide(TraceContext.get(), performanceLog)) {
            return;
        }

        sampler.execute(() -> {
            if (HawkEyeConfig.isPerformanceDisabled()) {
                log.info("{} Performance monitor disabled!!!", HAWK_EYE_COLLECTOR);
                return;
            }

            if (Objects.isNull(hawkEyePerformanceCollector) || !hawkEyePerformanceCollector.isAlive()) {
                log.info("{} {} not started or died!!!", HAWK_EYE_COLLECTOR, hawkEyePerformanceCollector.getName());
                return;
            }

            try {
                boolean success = BUFFER.offer(performanceLog);
                if (!success) {
                    log.warn("{} Performance log [{}] queue failed!!!",
                            HAWK_EYE_COLLECTOR, JsonUtils.toString(performanceLog));
                }
            } catch (Exception ignore) {
            }
        });
    }

}
