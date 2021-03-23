package com.littleyes.collector.worker;

import com.littleyes.collector.spi.PerformanceLogDelivery;
import com.littleyes.common.core.PluginLoader;
import com.littleyes.common.dto.PerformanceLogDto;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.BlockingQueue;

import static com.littleyes.collector.util.Constants.HAWK_EYE_COLLECTOR;

/**
 * <p> <b> 性能日志收集任务线程 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-19
 */
@Slf4j
public class HawkEyePerformanceCollector extends BaseCollector<PerformanceLogDto> {

    public HawkEyePerformanceCollector(BlockingQueue<PerformanceLogDto> bufferQueue) {
        super(HawkEyePerformanceCollector.class.getSimpleName(), bufferQueue);
    }

    @Override
    public void send(List<PerformanceLogDto> performanceLogs) {
        try {
            PluginLoader.of(PerformanceLogDelivery.class).load().deliver(performanceLogs);
        } catch (Exception e) {
            log.error("{} send performance logs error", HAWK_EYE_COLLECTOR, e);
        }
    }

}
