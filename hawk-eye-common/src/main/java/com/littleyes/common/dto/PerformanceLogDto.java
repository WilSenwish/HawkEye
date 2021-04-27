package com.littleyes.common.dto;

import com.littleyes.common.enums.DateTimeFormatterEnum;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

/**
 * <p> <b> 性能日志 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-20
 */
@EqualsAndHashCode
@ToString
@Getter
@Builder
public class PerformanceLogDto extends BaseDto implements Serializable {

    private static final long serialVersionUID = 147L;

    private String  event;
    private String  method;
    private Integer type;
    private boolean success;
    private long start;
    private long end;
    private long minute;
    private long timeConsume;

    private String body;

    @Override
    public void initBase() {
        super.initBase();
        minute      = DateTimeFormatterEnum.MINUTE.format(start);
        timeConsume = end - start;
    }

}
