package com.littleyes.common.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.Objects;

/**
 * <p> <b> SpringContext 工具类 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-23
 */
@Configuration
public class SpringContextHolder implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        initContext(applicationContext);
    }

    private static void initContext(ApplicationContext applicationContext) {
        context = applicationContext;
    }

    /**
     * 根据名称和类型获取 Bean
     *
     * @param beanName
     * @param requiredClazzType
     * @return
     */
    public static <T> T getBean(String beanName, Class<T> requiredClazzType) {
        if (StringUtils.isBlank(beanName)) {
            throw new NullPointerException("beanName must not empty!");
        }
        if (Objects.isNull(requiredClazzType)) {
            throw new NullPointerException("requiredClazzType must not null!");
        }

        return context.getBean(beanName, requiredClazzType);
    }

    /**
     * 根据类型获取 Bean
     *
     * @param requiredClazzType
     * @return
     */
    public static <T> T getBean(Class<T> requiredClazzType) {
        if (Objects.isNull(requiredClazzType)) {
            throw new NullPointerException("requiredClazzType must not null!");
        }

        return context.getBean(requiredClazzType);
    }

    /**
     * 根据类型获取 Bean
     *
     * @param requiredClazzType
     * @return
     */
    public static <T> Map<String, T> getBeansOfType(Class<T> requiredClazzType) {
        if (Objects.isNull(requiredClazzType)) {
            throw new NullPointerException("requiredClazzType must not null!");
        }

        return context.getBeansOfType(requiredClazzType);
    }

}
