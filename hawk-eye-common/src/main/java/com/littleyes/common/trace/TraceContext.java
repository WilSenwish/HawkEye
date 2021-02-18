package com.littleyes.common.trace;

import com.alibaba.ttl.TransmittableThreadLocal;

import java.util.Objects;

/**
 * @Description 上下文
 *
 * @author Junbing.Chen
 * @date 2021-02-18
 */
public class TraceContext {

    private static final ThreadLocal<TraceContext> TTC = TransmittableThreadLocal.withInitial(TraceContext::new);

    private String traceId;
    private boolean traceDebug;

    public String getTraceId() {
        return traceId;
    }

    private void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public boolean isTraceDebug() {
        return traceDebug;
    }

    private void setTraceDebug(boolean traceDebug) {
        this.traceDebug = traceDebug;
    }

    private TraceContext() {
    }

    public static TraceContext init(String traceId, boolean traceDebug) {
        TraceContext context = get();

        context.setTraceId(Objects.nonNull(traceId) ? traceId : DtiGenerator.generate());
        context.setTraceDebug(traceDebug);

        return context;
    }

    public static String traceId() {
        return get().getTraceId();
    }

    public static boolean traceDebugEnabled() {
        return get().isTraceDebug();
    }

    private static void set(TraceContext context) {
        TTC.set(context);
    }

    private static TraceContext get() {
        return TTC.get();
    }

    private static void remove() {
        TTC.remove();
    }

}
