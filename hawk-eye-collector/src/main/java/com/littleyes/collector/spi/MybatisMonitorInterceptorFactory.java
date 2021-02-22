package com.littleyes.collector.spi;

import com.littleyes.common.core.SPI;
import org.apache.ibatis.plugin.Interceptor;

/**
 * <p> <b> MybatisMonitorInterceptor 工厂 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-22
 */
@SPI
public interface MybatisMonitorInterceptorFactory {

    /**
     * 创建一个 MybatisMonitorInterceptor
     *
     * @return MybatisMonitorInterceptor 实例
     */
    Interceptor get();

}
