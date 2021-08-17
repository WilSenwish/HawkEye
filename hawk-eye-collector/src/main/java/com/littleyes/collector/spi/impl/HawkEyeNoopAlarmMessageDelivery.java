package com.littleyes.collector.spi.impl;

import com.littleyes.collector.spi.AlarmMessageDelivery;
import com.littleyes.common.core.SPI;
import com.littleyes.common.dto.AlarmMessageDto;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * <p> <b> 默认 AlarmMessage 传输器接口实现 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-08-16
 */
@Slf4j
@SPI
public class HawkEyeNoopAlarmMessageDelivery implements AlarmMessageDelivery {
    @Override
    public void deliver(List<AlarmMessageDto> messages) {
        if (log.isDebugEnabled()) {
            log.debug("Alarm Message: {}", messages);
        }
    }
}
