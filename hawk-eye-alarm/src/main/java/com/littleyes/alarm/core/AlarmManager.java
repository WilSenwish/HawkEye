package com.littleyes.alarm.core;

import com.littleyes.alarm.spi.Alarm;
import com.littleyes.common.config.HawkEyeConfig;
import com.littleyes.common.util.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.littleyes.alarm.util.Constants.HAWK_EYE_ALARM;

/**
 * <p> <b> 预警管理器 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-08-17
 */
@Slf4j
public class AlarmManager {

    private static final Map<String, Alarm> ALARMS = new LinkedHashMap<>();

    private AlarmManager() {
    }

    public static void schedule() {
        if (HawkEyeConfig.isAlarmDisabled()) {
            log.info("{} Alarm disabled", HAWK_EYE_ALARM);
            return;
        }

        ALARMS.clear();
        Map<String, Alarm> alarms = SpringContextHolder.getBeansOfType(Alarm.class);
        ALARMS.putAll(alarms);
        log.info("{} Loaded all Alarm", HAWK_EYE_ALARM);
    }

}
