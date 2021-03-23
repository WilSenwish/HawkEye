package com.littleyes.common.trace;

import com.alibaba.ttl.TransmittableThreadLocal;

import java.util.Objects;

/**
 * <p> <b> 上下文 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-18
 */
public class TraceContext {

    private static final ThreadLocal<TraceContext> TTC = TransmittableThreadLocal.withInitial(TraceContext::new);

    private String traceId;

    public String getTraceId() {
        return traceId;
    }

    private void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    private TraceContext() {
    }

    public static TraceContext init(String traceId) {
        TraceContext context = get();

        context.setTraceId(Objects.nonNull(traceId) ? traceId : DtiGenerator.generate());

        return context;
    }

    public static String traceId() {
        return get().getTraceId();
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
