package com.littleyes.alarm.spi;

import com.littleyes.common.dto.alarm.AlarmMessageDto;

/**
 * <p> <b> 报警接口 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-08-17
 */
public interface Alarm {

    AlarmMessageDto alarm();

}
