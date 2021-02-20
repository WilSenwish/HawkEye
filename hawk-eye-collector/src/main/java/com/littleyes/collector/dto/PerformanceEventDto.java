package com.littleyes.collector.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p> <b> 性能事件 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-20
 */
@Data
public class PerformanceEventDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String projectName;
    private Integer type;
    private List<String> events;

}
