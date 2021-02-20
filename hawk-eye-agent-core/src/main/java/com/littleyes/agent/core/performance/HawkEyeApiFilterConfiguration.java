package com.littleyes.agent.core.performance;

import com.littleyes.collector.spi.ApiFilterFactory;
import com.littleyes.common.core.PluginLoader;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import javax.servlet.Filter;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * <p> <b> HawkEye Api Filter 注册 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-20
 */
@Configuration
@ConditionalOnProperty(value = "hawk-eye.plugin.api-filter", havingValue = "enabled")
public class HawkEyeApiFilterConfiguration {

    private String excludeUrls      = Collections.unmodifiableList(Arrays.asList("/favicon.ico", "/v2/api-docs")).stream()
            .collect(Collectors.joining(","));
    private String excludePrefixes  = Collections.unmodifiableList(Arrays.asList("/webjars", "/swagger-resources", "/actuator")).stream()
            .collect(Collectors.joining(","));
    private String excludeSuffixes  = Collections.unmodifiableList(Arrays.asList(".css", ".js", ".jpg", ".jpeg", ".gif", ".png", ".jsp", ".asp", ".html", ".eot", ".svg", ".ttf", ".woff", ".woff2", ".map", ".sql")).stream()
            .collect(Collectors.joining(","));

    @Bean
    public FilterRegistrationBean<Filter> apiFilter() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();

        registration.setFilter(PluginLoader.of(ApiFilterFactory.class).load().filter());
        registration.addUrlPatterns("/*");
        registration.addInitParameter("excludeUrls", excludeUrls);
        registration.addInitParameter("excludePrefixes", excludePrefixes);
        registration.addInitParameter("excludeSuffixes", excludeSuffixes);
        registration.setName("apiFilter");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 147);

        return registration;
    }

}
