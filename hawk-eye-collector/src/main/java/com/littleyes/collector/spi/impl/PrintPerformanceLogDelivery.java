package com.littleyes.collector.spi.impl;

import com.littleyes.collector.dto.PerformanceLogDto;
import com.littleyes.collector.spi.PerformanceLogDelivery;
import com.littleyes.common.core.SPI;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.littleyes.collector.util.Constants.HAWK_EYE_COLLECTOR;

/**
 * <p> <b> 默认 Performance 日志传输器接口实现 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-19
 */
@SPI
@Slf4j
public class PrintPerformanceLogDelivery implements PerformanceLogDelivery {

    @Override
    public void deliver(List<PerformanceLogDto> performanceLogs) {
        if (log.isDebugEnabled()) {
            log.debug("{} Performance log size [{}]", HAWK_EYE_COLLECTOR, performanceLogs.size());
        }
    }

}
