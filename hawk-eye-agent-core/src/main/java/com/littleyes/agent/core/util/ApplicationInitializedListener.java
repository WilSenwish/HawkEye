package com.littleyes.agent.core.util;

import com.littleyes.collector.util.Mappings;
import com.littleyes.threadpool.util.HawkEyeForkJoinPools;
import lombok.extern.slf4j.Slf4j;
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
public class ApplicationInitializedListener implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        HawkEyeForkJoinPools.monitorForkJoinPoolOfCommonPool();

        RequestMappingHandlerMapping handlerMapping = event.getApplicationContext()
                .getBean(RequestMappingHandlerMapping.class);

        List<String> urls = handlerMapping.getHandlerMethods().keySet().stream()
                .flatMap(e -> e.getPatternsCondition().getPatterns().stream())
                .collect(Collectors.toList());
        Mappings.fill(urls);
        log.info("{} Application Initialized with mapping size [{}]", HAWK_EYE_AGENT, urls.size());
    }

}
