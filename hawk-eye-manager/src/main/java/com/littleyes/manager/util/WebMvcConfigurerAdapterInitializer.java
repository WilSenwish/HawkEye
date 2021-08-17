package com.littleyes.manager.util;

import com.littleyes.manager.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * <p> <b> WebMvc 配置 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-03-17
 */
@Configuration
public class WebMvcConfigurerAdapterInitializer implements WebMvcConfigurer {

    @Autowired
    private AuthService authService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry
                .addInterceptor(new AuthenticationInterceptor(authService))
                .addPathPatterns("/**")
                .excludePathPatterns("/auth/**")
                .excludePathPatterns("/hawk-eye/health/**")
                .order(Ordered.LOWEST_PRECEDENCE);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
                .addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
