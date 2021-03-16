package com.littleyes.collector.worker;

import com.littleyes.collector.dto.LoggingLogDto;
import com.littleyes.collector.spi.LoggingLogDelivery;
import com.littleyes.common.core.PluginLoader;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

import static com.littleyes.collector.util.Constants.HAWK_EYE_COLLECTOR;

/**
 * <p> <b> 监控日志收集任务线程 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-19
 */
@Slf4j
public class HawkEyeLoggingCollector extends BaseCollector<LoggingLogDto> {

    public HawkEyeLoggingCollector(BlockingQueue<LoggingLogDto> bufferQueue) {
        super(HawkEyeLoggingCollector.class.getSimpleName(), bufferQueue);
    }

    @Override
    public void send(List<LoggingLogDto> loggingLogs) {
        try {
            PluginLoader.of(LoggingLogDelivery.class).load()
                    .deliver(loggingLogs.parallelStream().peek(LoggingLogDto::initBase).collect(Collectors.toList()));
        } catch (Exception e) {
            log.error("{} send logging logs error", HAWK_EYE_COLLECTOR, e);
        }
    }

}
