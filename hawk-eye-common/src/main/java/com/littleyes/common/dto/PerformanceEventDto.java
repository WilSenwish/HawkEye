package com.littleyes.common.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * <p> <b> 性能事件 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-20
 */
@EqualsAndHashCode
@ToString
@Getter
@Builder
public class PerformanceEventDto implements Serializable {

    private static final long serialVersionUID = 147L;

    private String  projectName;
    private Integer type;
    private List<String> events;

}
