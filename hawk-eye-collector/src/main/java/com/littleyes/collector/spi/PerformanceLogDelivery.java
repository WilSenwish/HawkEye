package com.littleyes.collector.spi;

import com.littleyes.common.dto.PerformanceLogDto;
import com.littleyes.common.core.SPI;

import java.util.List;

/**
 * <p> <b> Performance 日志传输器接口 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-19
 */
@SPI
public interface PerformanceLogDelivery {

    /**
     * 传输 Performance 日志
     *
     * @param performanceLogs Performance 日志列表
     */
    default void deliver(List<PerformanceLogDto> performanceLogs) {}

}
