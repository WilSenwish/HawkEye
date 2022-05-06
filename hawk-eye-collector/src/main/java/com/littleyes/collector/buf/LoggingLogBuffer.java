package com.littleyes.collector.buf;

import com.littleyes.common.dto.LoggingLogDto;
import com.littleyes.collector.worker.HawkEyeLoggingCollector;
import com.littleyes.common.config.HawkEyeConfig;
import com.littleyes.common.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static com.littleyes.collector.util.Constants.BUFFER_MAX_CAPACITY;
import static com.littleyes.collector.util.Constants.HAWK_EYE_COLLECTOR;

/**
 * <p> <b> 监控日志缓存 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-22
 */
@Slf4j
public class LoggingLogBuffer {

    private static final BlockingQueue<LoggingLogDto> BUFFER = new ArrayBlockingQueue<>(BUFFER_MAX_CAPACITY);
    private static HawkEyeLoggingCollector hawkEyeLoggingCollector;

    static {
        initialize();
    }

    private LoggingLogBuffer() {
    }

    private static void initialize() {
        if (HawkEyeConfig.isLoggingDisabled()) {
            log.info("{} Logging monitor disabled!!!", HAWK_EYE_COLLECTOR);
            return;
        }

        if (Objects.nonNull(hawkEyeLoggingCollector)) {
            log.info("{} {} already started!!!", HAWK_EYE_COLLECTOR, hawkEyeLoggingCollector.getName());
            return;
        }

        hawkEyeLoggingCollector = new HawkEyeLoggingCollector(BUFFER);
        hawkEyeLoggingCollector.start();
    }

    public static void offer(LoggingLogDto loggingLog) {
        if (HawkEyeConfig.isLoggingDisabled()) {
            log.info("{} Logging monitor disabled!!!", HAWK_EYE_COLLECTOR);
            return;
        }

        if (Objects.isNull(hawkEyeLoggingCollector) || !hawkEyeLoggingCollector.isAlive()) {
            log.info("{} {} not started or died!!!", HAWK_EYE_COLLECTOR, hawkEyeLoggingCollector.getName());
            return;
        }

        try {
            // 链路信息
            loggingLog.initTrace();

            boolean success = BUFFER.offer(loggingLog);
            if (!success) {
                log.warn("{} Logging log [{}] queue failed!!!", HAWK_EYE_COLLECTOR, JsonUtils.toString(loggingLog));
            }
        } catch (Exception ignore) {
        }
    }

}
