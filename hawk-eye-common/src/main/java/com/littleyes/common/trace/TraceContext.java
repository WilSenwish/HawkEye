package com.littleyes.common.trace;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.littleyes.common.dto.PerformanceLogDto;
import com.littleyes.common.util.PerformanceContext;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.littleyes.common.config.HawkEyeConfig.HAWK_EYE_COMMON;

/**
 * <p> <b> 上下文 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-18
 */
@Slf4j
public class TraceContext {

    private static final ThreadLocal<TraceContext> TTLC = TransmittableThreadLocal.withInitial(TraceContext::new);


    private String  traceId;
    private boolean debugMode;
    private List<PerformanceLogDto> performanceLogs = new ArrayList<>();


    public String getTraceId() {
        return traceId;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public List<PerformanceLogDto> getPerformanceLogs() {
        return performanceLogs;
    }

    public void addPerformanceLog(PerformanceLogDto performanceLog) {
        this.performanceLogs.add(performanceLog);
        if (isDebugMode()) {
            log.info("{} {}", HAWK_EYE_COMMON, performanceLog);
        }
    }

    private TraceContext() {
    }


    public static TraceContext init(String traceId) {
        TraceContext context = get();

        context.traceId = Objects.nonNull(traceId) ? traceId : DtiGenerator.generate();

        return context;
    }


    public static String traceId() {
        return get().traceId;
    }

    public static boolean debugMode() {
        return get().debugMode;
    }

    public static void append(int type) {
        get().addPerformanceLog(PerformanceContext.buildPerformanceLog(type));
    }

    public static List<PerformanceLogDto> performanceLogs() {
        return get().performanceLogs;
    }


    public static TraceContext get() {
        return TTLC.get();
    }

    public static void remove() {
        TTLC.remove();
    }

}
