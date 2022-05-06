package com.littleyes.common.trace;

import com.alibaba.ttl.TransmittableThreadLocal;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

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
    private boolean needSample;


    private TraceContext() {
    }


    public String getTraceId() {
        return traceId;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public boolean isNeedSample() {
        return needSample;
    }

    public void setNeedSample(boolean needSample) {
        this.needSample = needSample;
    }


    public static TraceContext init(String traceId, boolean debugMode) {
        TraceContext context    = get();

        context.traceId         = Objects.nonNull(traceId) ? traceId : DtiGenerator.generate();
        context.debugMode       = debugMode;

        return context;
    }


    public static String traceId() {
        return get().traceId;
    }

    public static boolean debugMode() {
        return get().debugMode;
    }

    public static boolean needSample() {
        return get().needSample;
    }


    public static TraceContext get() {
        return TTLC.get();
    }

    public static void remove() {
        TTLC.remove();
    }

}
