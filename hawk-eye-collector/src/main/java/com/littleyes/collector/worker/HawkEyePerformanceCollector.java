package com.littleyes.collector.worker;

import com.littleyes.collector.dto.PerformanceLogDto;
import com.littleyes.collector.spi.PerformanceLogDelivery;
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
 * <p> <b> 性能日志收集任务线程 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-19
 */
@Slf4j
public class HawkEyePerformanceCollector extends Thread {

    private final BlockingQueue<PerformanceLogDto> bufferQueue;

    public HawkEyePerformanceCollector(BlockingQueue<PerformanceLogDto> bufferQueue) {
        super("HawkEyePerformanceCollector");
        this.bufferQueue = bufferQueue;
    }

    @Override
    public void run() {
        if (HawkEyeConfig.isPerformanceDisabled()) {
            return;
        }

        boolean interrupted = false;

        try {
            TimeUnit.SECONDS.sleep(3L);
        } catch (InterruptedException e) {
            interrupted = true;
        }

        log.info("{} {} running...", HAWK_EYE_COLLECTOR, getName());

        List<PerformanceLogDto> performanceLogs = new LinkedList<>();

        if (!interrupted) {
            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }

                try {
                    bufferQueue.drainTo(performanceLogs, (BUFFER_PROCESS_SIZE << 1));
                    if (HawkEyeCollectionUtils.isEmpty(performanceLogs)) {
                        TimeUnit.MILLISECONDS.sleep(HawkEyeConfig.getCollectorSpinWaitMills());
                        continue;
                    }

                    sendLogs(performanceLogs);
                } catch (InterruptedException ignore) {
                    break;
                } catch (Exception e) {
                    log.error("{} Process logging log error {}", HAWK_EYE_COLLECTOR, e.getMessage(), e);
                }

                performanceLogs.clear();
            }
        }

        log.error("{} {} will exit!!!", HAWK_EYE_COLLECTOR, getName());

        try {
            bufferQueue.drainTo(performanceLogs, (BUFFER_PROCESS_SIZE << 1));
            if (HawkEyeCollectionUtils.isNotEmpty(performanceLogs)) {
                sendLogs(performanceLogs);
            }
        } catch (Exception e) {
            log.error("{} {}", HAWK_EYE_COLLECTOR, e.getMessage(), e);
        } finally {
            performanceLogs.clear();
        }
    }

    private void sendLogs(List<PerformanceLogDto> performanceLogs) {
        try {
            PluginLoader.of(PerformanceLogDelivery.class).load()
                    .deliver(performanceLogs.parallelStream().peek(PerformanceLogDto::initBase).collect(Collectors.toList()));
        } catch (Exception e) {
            log.error("{} send logs error", HAWK_EYE_COLLECTOR, e);
        }
    }

}
