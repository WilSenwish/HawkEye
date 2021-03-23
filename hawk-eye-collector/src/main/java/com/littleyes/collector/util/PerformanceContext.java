package com.littleyes.collector.util;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.littleyes.common.dto.PerformanceLogDto;
import com.littleyes.common.enums.PerformanceTypeEnum;
import com.littleyes.common.util.JsonUtils;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p> <b> 性能日志上下文 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-22
 */
@Accessors(chain = true)
public class PerformanceContext {

    private static Map<Integer, ThreadLocal<PerformanceContext>> allContext = new ConcurrentHashMap<>();

    private String event;
    private String method;
    private int type;
    private boolean success;
    private long start;
    private long end;

    @Setter
    private String sql;
    @Setter
    private Map<String, Object> parameters = Collections.emptyMap();

    private String getBody() {
        if (PerformanceTypeEnum.API.getType().equals(type)) {
            return JsonUtils.toString(parameters);
        } else if (PerformanceTypeEnum.MYSQL.getType().equals(type)) {
            return sql;
        }

        return StringUtils.EMPTY;
    }

    private PerformanceContext(String event, String method, int type, boolean success, long start, long end) {
        this.event = event;
        this.method = method;
        this.type = type;
        this.success = success;
        this.start = start;
        this.end = end;
    }

    public static PerformanceContext init(String event, String method, int type, boolean success, long start, long end) {
        PerformanceContext context = new PerformanceContext(event, method, type, success, start, end);

        allContext.putIfAbsent(type, new TransmittableThreadLocal<>());
        allContext.get(type).set(context);

        return context;
    }

    public static PerformanceLogDto buildPerformanceLog(int type) {
        PerformanceContext context = allContext.get(type).get();

        PerformanceLogDto performanceLog = PerformanceLogDto.builder()
                .event(context.event)
                .method(context.method)
                .type(context.type)
                .success(context.success)
                .start(context.start)
                .end(context.end)
                .body(context.getBody())
                .build();

        performanceLog.initTrace();

        return performanceLog;
    }

}
