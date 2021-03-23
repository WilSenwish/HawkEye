package com.littleyes.common.trace;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.littleyes.common.dto.PerformanceLogDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p> <b> 上下文 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-18
 */
public class TraceContext {

    private static final ThreadLocal<TraceContext> TTLC = TransmittableThreadLocal.withInitial(TraceContext::new);


    private String  traceId;
    private boolean debugMode;
    private boolean occursError;
    private long    serviceTimeMillis;
    private List<PerformanceLogDto> performanceLogs = new ArrayList<>();


    public String getTraceId() {
        return traceId;
    }

    private void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public boolean isOccursError() {
        return occursError;
    }

    public void setOccursError(boolean occursError) {
        this.occursError = occursError;
    }

    public long getServiceTimeMillis() {
        return serviceTimeMillis;
    }

    public void setServiceTimeMillis(long serviceTimeMillis) {
        this.serviceTimeMillis = serviceTimeMillis;
    }

    public List<PerformanceLogDto> getPerformanceLogs() {
        return performanceLogs;
    }

    public void addPerformanceLog(PerformanceLogDto performanceLog) {
        this.performanceLogs.add(performanceLog);
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

    public static boolean occursError() {
        return get().occursError;
    }

    public static long serviceTimeMillis() {
        return get().serviceTimeMillis;
    }

    public static List<PerformanceLogDto> performanceLogs() {
        return get().performanceLogs;
    }


    private static void set(TraceContext context) {
        TTLC.set(context);
    }

    private static TraceContext get() {
        return TTLC.get();
    }

    private static void remove() {
        TTLC.remove();
    }

}
