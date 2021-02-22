package com.littleyes.collector.worker;

import com.littleyes.collector.dto.LoggingLogDto;
import com.littleyes.collector.spi.LoggingLogDelivery;
import com.littleyes.common.config.HawkEyeConfig;
import com.littleyes.common.core.PluginLoader;
import com.littleyes.common.util.HawkEyeCollectionUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.littleyes.collector.util.Constants.BUFFER_PROCESS_SIZE;
import static com.littleyes.collector.util.Constants.HAWK_EYE_COLLECTOR;

/**
 * <p> <b> 监控日志收集任务线程 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-19
 */
@Slf4j
public class HawkEyeLoggingCollector extends Thread {

    private final BlockingQueue<LoggingLogDto> bufferQueue;

    public HawkEyeLoggingCollector(BlockingQueue<LoggingLogDto> bufferQueue) {
        super("HawkEyeLoggingCollector");
        this.bufferQueue = bufferQueue;
    }

    @Override
    public void run() {
        if (HawkEyeConfig.isLoggingDisabled()) {
            return;
        }

        boolean interrupted = false;

        try {
            TimeUnit.SECONDS.sleep(3L);
        } catch (InterruptedException e) {
            interrupted = true;
        }

        log.info("{} {} running...", HAWK_EYE_COLLECTOR, getName());

        List<LoggingLogDto> loggingLogs = new LinkedList<>();

        if (!interrupted) {
            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }

                try {
                    bufferQueue.drainTo(loggingLogs, BUFFER_PROCESS_SIZE);
                    if (HawkEyeCollectionUtils.isEmpty(loggingLogs)) {
                        TimeUnit.MILLISECONDS.sleep(HawkEyeConfig.getCollectorSpinWaitMills());
                        continue;
                    }

                    sendLogs(loggingLogs);
                } catch (InterruptedException ignore) {
                    break;
                } catch (Exception e) {
                    log.error("{} Process logging log error {}", HAWK_EYE_COLLECTOR, e.getMessage(), e);
                }

                loggingLogs.clear();
            }
        }

        log.error("{} {} will exit!!!", HAWK_EYE_COLLECTOR, getName());

        try {
            bufferQueue.drainTo(loggingLogs, BUFFER_PROCESS_SIZE);
            if (HawkEyeCollectionUtils.isNotEmpty(loggingLogs)) {
                sendLogs(loggingLogs);
            }
        } catch (Exception e) {
            log.error("{} {}", HAWK_EYE_COLLECTOR, e.getMessage(), e);
        } finally {
            loggingLogs.clear();
        }
    }

    private void sendLogs(List<LoggingLogDto> loggingLogs) {
        try {
            PluginLoader.of(LoggingLogDelivery.class).load()
                    .deliver(loggingLogs.parallelStream().peek(LoggingLogDto::initBase).collect(Collectors.toList()));
        } catch (Exception e) {
            log.error("{} send logs error", HAWK_EYE_COLLECTOR, e);
        }
    }

}
