package com.littleyes.agent.core.util;

import com.littleyes.agent.core.jvm.JvmMetricProvider;
import com.littleyes.collector.util.Mappings;
import com.littleyes.common.config.HawkEyeConfig;
import com.littleyes.threadpool.util.HawkEyeForkJoinPools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.List;
import java.util.stream.Collectors;

import static com.littleyes.agent.core.util.Constants.HAWK_EYE_AGENT;

/**
 * <p> <b> 上下文初始化监听器 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-20
 */
@Slf4j
@Component
public class ApplicationInitializerListener implements ApplicationListener<ApplicationEvent> {

    @Autowired
    private ServerProperties serverProperties;

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextRefreshedEvent) {
            onContextRefreshed((ContextRefreshedEvent) event);
        } else if (event instanceof WebServerInitializedEvent) {
            onWebServerInitialized((WebServerInitializedEvent) event);
        }
    }

    private void onContextRefreshed(ContextRefreshedEvent event) {
        monitorJvm();

        extractRequestMapping4Use(event);

        monitorForkJoinPoolOfCommonPool();
    }

    private void onWebServerInitialized(WebServerInitializedEvent event) {
        HawkEyeConfig.recordServerPort(serverProperties.getPort());
        log.info("{} Current Application Port [{}]", HAWK_EYE_AGENT, serverProperties.getPort());
    }

    private void monitorJvm() {
        JvmMetricProvider.monitorJvm();
    }

    private void monitorForkJoinPoolOfCommonPool() {
        HawkEyeForkJoinPools.monitorForkJoinPoolOfCommonPool();
    }

    private void extractRequestMapping4Use(ContextRefreshedEvent event) {
        RequestMappingHandlerMapping handlerMapping = event.getApplicationContext()
                .getBean(RequestMappingHandlerMapping.class);

        List<String> mappings = handlerMapping.getHandlerMethods().keySet().stream()
                .flatMap(e -> e.getPatternsCondition().getPatterns().stream())
                .collect(Collectors.toList());
        Mappings.fill(mappings);

        log.info("{} Current Application RequestMapping size [{}]", HAWK_EYE_AGENT, mappings.size());
    }

}
