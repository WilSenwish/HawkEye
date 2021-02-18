package com.littleyes.common.trace;

public class TestTraceContext {
    public static void main(String[] args) {
        TraceContext context1 = TraceContext.init("12345", true);
        System.out.println(TraceContext.traceId() == context1.getTraceId());
        System.out.println(TraceContext.traceId());
        TraceContext context2 = TraceContext.init(null, true);
        System.out.println(context2.getTraceId() == TraceContext.traceId());
        System.out.println(context2.getTraceId());
        TraceContext context3 = TraceContext.init(null, false);
        System.out.println(context3.isTraceDebug() == TraceContext.traceDebugEnabled());
        System.out.println(context3.isTraceDebug());
        System.out.println(context3.getTraceId());
    }
}
