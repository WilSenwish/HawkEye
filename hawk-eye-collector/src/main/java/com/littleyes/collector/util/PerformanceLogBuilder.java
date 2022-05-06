package com.littleyes.collector.util;

import com.littleyes.common.dto.PerformanceLogDto;
import com.littleyes.common.enums.PerformanceTypeEnum;

/**
 * <p> <b> PerformanceLogBuilder </b> </p>
 *
 * @author Junbing.Chen
 * @date 2022-05-06
 */
public class PerformanceLogBuilder {

    public static PerformanceLogDto build(String event,
                                          String method,
                                          PerformanceTypeEnum type,
                                          boolean success,
                                          long start,
                                          long end) {

        PerformanceLogDto performanceLog = PerformanceLogDto.builder()
                .event(event)
                .method(method)
                .type(type.getType())
                .success(success)
                .start(start)
                .end(end)
                .build();

        performanceLog.initTrace();
        performanceLog.initBase();

        return performanceLog;
    }

}
