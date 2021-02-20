package com.littleyes.collector.spi.impl;

import com.littleyes.collector.core.HawkEyeApiFilter;
import com.littleyes.collector.spi.ApiFilterFactory;
import com.littleyes.common.core.SPI;

import javax.servlet.Filter;

/**
 * <p> <b> Api Filter 工厂接口实现 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-19
 */
@SPI
public class HawkEyeApiFilterFactory implements ApiFilterFactory {

    @Override
    public Filter filter() {
        return new HawkEyeApiFilter();
    }

}
