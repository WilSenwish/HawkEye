package com.littleyes.collector.worker;

import com.littleyes.common.config.HawkEyeConfig;
import com.littleyes.common.util.HawkEyeCollectionUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static com.littleyes.collector.util.Constants.BUFFER_PROCESS_SIZE;
import static com.littleyes.collector.util.Constants.HAWK_EYE_COLLECTOR;

/**
 * <p> <b> 监控采样数据收集任务基础线程 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-03-16
 */
@Slf4j
public abstract class BaseCollector<T> extends Thread {

    private final BlockingQueue<T> bufferQueue;

    public BaseCollector(String collectorName, BlockingQueue<T> bufferQueue) {
        super(collectorName);
        this.bufferQueue = bufferQueue;
    }

    @Override
    public final void run() {
        if (HawkEyeConfig.isPerformanceDisabled()) {
            log.info("{} {} disabled for Performance Disabled", HAWK_EYE_COLLECTOR, getName());
            return;
        }

        boolean interrupted = false;

        try {
            TimeUnit.SECONDS.sleep(3L);
        } catch (InterruptedException e) {
            interrupted = true;
        }

        log.info("{} {} running...", HAWK_EYE_COLLECTOR, getName());

        List<T> sampleDataList = new ArrayList<>((BUFFER_PROCESS_SIZE << 1));

        if (!interrupted) {
            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }

                try {
                    bufferQueue.drainTo(sampleDataList, BUFFER_PROCESS_SIZE);
                    if (HawkEyeCollectionUtils.isEmpty(sampleDataList)) {
                        TimeUnit.MILLISECONDS.sleep(getCollectorSpinWaitMills());
                        continue;
                    }

                    send(sampleDataList);
                } catch (InterruptedException ignore) {
                    break;
                } catch (Exception e) {
                    log.error("{} {} Loop process error {}", HAWK_EYE_COLLECTOR, getName(), e.getMessage(), e);
                }

                sampleDataList.clear();
            }
        }

        log.error("{} {} will exit!!!", HAWK_EYE_COLLECTOR, getName());

        try {
            bufferQueue.drainTo(sampleDataList, (BUFFER_PROCESS_SIZE << 1));
            if (HawkEyeCollectionUtils.isNotEmpty(sampleDataList)) {
                send(sampleDataList);
            }
        } catch (Exception e) {
            log.error("{} {} Final Process error {}", HAWK_EYE_COLLECTOR, getName(), e.getMessage(), e);
        } finally {
            sampleDataList.clear();
        }
    }

    /**
     * 收集休息时间间隔
     *
     * @return
     */
    protected long getCollectorSpinWaitMills() {
        return HawkEyeConfig.getCollectorSpinWaitMills();
    }

    /**
     * 数据发送
     *
     * @param sampleDataList
     */
    protected abstract void send(List<T> sampleDataList);

}
