package com.littleyes.collector.spi.impl;

import com.littleyes.collector.spi.LoggingLogDelivery;
import com.littleyes.common.core.SPI;
import com.littleyes.common.dto.LoggingLogDto;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * <p> <b> 默认 Logging 日志传输器接口实现 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-19
 */
@Slf4j
@SPI
public class HawkEyeNoopLoggingLogDelivery implements LoggingLogDelivery {
    @Override
    public void deliver(List<LoggingLogDto> loggingLogs) {
        if (log.isDebugEnabled()) {
            log.debug("Logging: {}", loggingLogs);
        }
    }
}
