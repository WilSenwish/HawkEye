package com.littleyes.collector.spi.impl;

import com.littleyes.collector.dto.LoggingLogDto;
import com.littleyes.collector.spi.LoggingLogDelivery;
import com.littleyes.common.core.SPI;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.littleyes.collector.util.Constants.HAWK_EYE_COLLECTOR;

/**
 * <p> <b> 默认 Logging 日志传输器接口实现 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-19
 */
@SPI
@Slf4j
public class PrintLoggingLogDelivery implements LoggingLogDelivery {

    @Override
    public void deliver(List<LoggingLogDto> loggingLogs) {
        if (log.isDebugEnabled()) {
            log.debug("{} Logging log size [{}]", HAWK_EYE_COLLECTOR, loggingLogs.size());
        }
    }

}
