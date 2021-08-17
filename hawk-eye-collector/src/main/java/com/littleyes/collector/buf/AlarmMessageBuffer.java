package com.littleyes.collector.buf;

import com.littleyes.collector.worker.HawkEyeAlarmMessageCollector;
import com.littleyes.common.config.HawkEyeConfig;
import com.littleyes.common.dto.alarm.AlarmMessageDto;
import com.littleyes.common.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static com.littleyes.collector.util.Constants.BUFFER_MAX_CAPACITY;
import static com.littleyes.collector.util.Constants.HAWK_EYE_COLLECTOR;

/**
 * <p> <b> AlarmMessage 缓存 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-03-16
 */
@Slf4j
public class AlarmMessageBuffer {

    private static final BlockingQueue<AlarmMessageDto> BUFFER = new ArrayBlockingQueue<>(BUFFER_MAX_CAPACITY >> 6);
    private static HawkEyeAlarmMessageCollector hawkEyeAlarmMessageCollector;

    static {
        new AlarmMessageBuffer();
    }

    private AlarmMessageBuffer() {
        if (HawkEyeConfig.isAlarmDisabled()) {
            log.info("{} Alarm disabled!!!", HAWK_EYE_COLLECTOR);
            return;
        }

        if (Objects.nonNull(hawkEyeAlarmMessageCollector)) {
            log.info("{} {} already started!!!", HAWK_EYE_COLLECTOR, hawkEyeAlarmMessageCollector.getName());
            return;
        }

        hawkEyeAlarmMessageCollector = new HawkEyeAlarmMessageCollector(BUFFER);
        hawkEyeAlarmMessageCollector.start();
    }

    public static void offer(AlarmMessageDto alarmMessage) {
        if (HawkEyeConfig.isAlarmDisabled()) {
            log.info("{} Alarm disabled!!!", HAWK_EYE_COLLECTOR);
            return;
        }

        if (Objects.isNull(hawkEyeAlarmMessageCollector) || !hawkEyeAlarmMessageCollector.isAlive()) {
            log.info("{} {} not started or died!!!", HAWK_EYE_COLLECTOR, hawkEyeAlarmMessageCollector.getName());
            return;
        }

        try {
            boolean success = BUFFER.offer(alarmMessage);
            if (!success) {
                log.warn("{} Alarm Message [{}] queue failed!!!", HAWK_EYE_COLLECTOR, JsonUtils.toString(alarmMessage));
            }
        } catch (Exception ignore) {
        }
    }

}
