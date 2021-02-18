package com.littleyes.common.ep;

import java.lang.annotation.*;

/**
 * <p> <b> 插件 SPI 注解 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-18
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SPI {

    /**
     * 加载顺序
     *
     * @return Ordered.HIGHEST_PRECEDENCE to Ordered.LOWEST_PRECEDENCE, default 0
     */
    int order() default 0;

}
