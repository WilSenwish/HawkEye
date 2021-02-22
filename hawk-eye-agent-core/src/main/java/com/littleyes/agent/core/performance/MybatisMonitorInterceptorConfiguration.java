package com.littleyes.agent.core.performance;

import com.littleyes.collector.spi.MybatisMonitorInterceptorFactory;
import com.littleyes.common.core.PluginLoader;
import org.apache.ibatis.plugin.Interceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p> <b> Mybatis 监控拦截器注入 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-22
 */
@Configuration
@ConditionalOnProperty(value = "hawk-eye.plugin.mybatis-interceptor", havingValue = "enabled")
public class MybatisMonitorInterceptorConfiguration {

    @Bean
    public Interceptor mybatisMonitorInterceptor() {
        return PluginLoader.of(MybatisMonitorInterceptorFactory.class).load().get();
    }

}
