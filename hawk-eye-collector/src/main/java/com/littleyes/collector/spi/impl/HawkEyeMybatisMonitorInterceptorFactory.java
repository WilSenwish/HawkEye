package com.littleyes.collector.spi.impl;

import com.littleyes.collector.core.MybatisMonitorInterceptor;
import com.littleyes.collector.spi.MybatisMonitorInterceptorFactory;
import com.littleyes.common.core.SPI;
import org.apache.ibatis.plugin.Interceptor;

/**
 * @Description MybatisMonitorInterceptor 工厂实现
 *
 * @author Junbing.Chen
 * @date 2021-01-08
 */
@SPI
public class HawkEyeMybatisMonitorInterceptorFactory implements MybatisMonitorInterceptorFactory {

    @Override
    public Interceptor get() {
        return new MybatisMonitorInterceptor();
    }

}
