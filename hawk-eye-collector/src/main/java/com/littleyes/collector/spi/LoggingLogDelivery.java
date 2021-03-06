package com.littleyes.collector.spi;

import com.littleyes.common.dto.LoggingLogDto;
import com.littleyes.common.core.SPI;

import java.util.List;

/**
 * <p> <b> Logging 日志传输器接口 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-19
 */
@SPI
public interface LoggingLogDelivery {

    /**
     * 传输 Logging 日志
     *
     * @param loggingLogs Logging 日志列表
     */
    default void deliver(List<LoggingLogDto> loggingLogs) {}

}
