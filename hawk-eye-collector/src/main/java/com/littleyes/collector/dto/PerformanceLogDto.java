package com.littleyes.collector.dto;

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
    private Integer type;
    private Boolean success;
    private Long start;
    private Long end;
    private Long minute;
    private Long timeConsume;

    private String body;

}
