package com.littleyes.collector.spi.impl;

import com.littleyes.collector.spi.PerformanceLogDelivery;
import com.littleyes.common.core.SPI;
import com.littleyes.common.dto.PerformanceLogDto;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * <p> <b> 默认 Performance 日志传输器接口实现 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-19
 */
@Slf4j
@SPI
public class HawkEyeNoopPerformanceLogDelivery implements PerformanceLogDelivery {
    @Override
    public void deliver(List<PerformanceLogDto> performanceLogs) {
        if (log.isDebugEnabled()) {
            log.debug("Performance: {}", performanceLogs);
        }
    }
}
