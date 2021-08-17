package com.littleyes.common.dto.alarm;

import com.littleyes.common.dto.BaseDto;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

/**
 * <p> <b>预警消息【注意，不要随便更换字段顺序，如需要添加字段，append】</b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-08-17
 */
@EqualsAndHashCode(callSuper = true)
@ToString
@Getter
@Builder
public class AlarmMessageDto extends BaseDto implements Serializable {

    private static final long serialVersionUID = 147L;

    private long start;
    private long end;

    private String metric;
    private MetricValType metricValType;
    private Object metricVal;

    private boolean abnormal;
    private String  abnormalMessage;

}
