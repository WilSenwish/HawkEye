package com.littleyes.alarm.core;

/**
 * <p> <b> 预警注解 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-08-17
 */
public @interface AlarmDefinition {

    String value();

    String cron() default "";

}
