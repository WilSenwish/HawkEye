package com.littleyes.collector.spi.impl;

import com.littleyes.collector.spi.JvmMetricDelivery;
import com.littleyes.common.core.SPI;
import com.littleyes.common.dto.jvm.JvmMetric;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * <p> <b> 默认 JvmMetric 传输器接口实现 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-03-16
 */
@Slf4j
@SPI
public class HawkEyeNoopJvmMetricDelivery implements JvmMetricDelivery {
    @Override
    public void deliver(List<JvmMetric> metrics) {
        if (log.isDebugEnabled()) {
            log.debug("Jvm Metric: {}", metrics);
        }
    }
}
