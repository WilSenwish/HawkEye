package com.littleyes.collector.spi;

import com.littleyes.common.core.SPI;

import javax.servlet.Filter;

/**
 * <p> <b> Api Filter 工厂接口 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-19
 */
@SPI
public interface ApiFilterFactory {

    /**
     * 创建 API Filter
     *
     * @return
     */
    Filter get();

}
