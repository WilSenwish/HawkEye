package com.littleyes.collector.dto;

import com.littleyes.common.enums.DateTimeFormatterEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * <p> <b> 性能日志 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-20
 */
@Data
public class PerformanceLogDto extends BaseDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String event;
    private String method;
    private int type;
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
