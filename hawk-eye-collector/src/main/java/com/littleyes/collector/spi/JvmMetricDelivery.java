package com.littleyes.collector.spi;

import com.littleyes.common.dto.jvm.JvmMetric;
import com.littleyes.common.core.SPI;

import java.util.List;

/**
 * <p> <b> JvmMetric 传输器接口 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-03-16
 */
@SPI
public interface JvmMetricDelivery {

    /**
     * 传输 JvmMetric
     *
     * @param metrics JvmMetric 列表
     */
    default void deliver(List<JvmMetric> metrics) {}

}
