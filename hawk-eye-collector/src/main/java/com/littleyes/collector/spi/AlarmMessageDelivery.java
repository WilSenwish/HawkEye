package com.littleyes.collector.spi;

import com.littleyes.common.core.SPI;
import com.littleyes.common.dto.alarm.AlarmMessageDto;

import java.util.List;

/**
 * <p> <b> AlarmMessage 传输器接口 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-08-16
 */
@SPI
public interface AlarmMessageDelivery {

    /**
     * 传输 AlarmMessage
     *
     * @param messages AlarmMessage 列表
     */
    default void deliver(List<AlarmMessageDto> messages) {}

}
