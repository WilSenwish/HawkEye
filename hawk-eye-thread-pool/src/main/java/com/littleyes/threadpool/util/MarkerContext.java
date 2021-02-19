package com.littleyes.threadpool.util;

import java.util.Objects;

/**
 * <p> <b> 标记上下文 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-19
 */
public class MarkerContext {

    private static final ThreadLocal<String> TASK_NAME_MARKS    = new ThreadLocal<>();
    private static final ThreadLocal<Long> TASK_TIME_MARKS      = ThreadLocal.withInitial(System::currentTimeMillis);

    public static void mark(String value) {
        TASK_NAME_MARKS.set(value);
    }

    public static void markIfEmpty(String value) {
        if (Objects.isNull(getMarker())) {
            mark(value);
        }
    }

    public static String getMarker() {
        return TASK_NAME_MARKS.get();
    }

    public static void removeMark() {
        TASK_NAME_MARKS.remove();
    }

    public static long begin() {
        return TASK_TIME_MARKS.get();
    }

    public static long getBeginTime() {
        return TASK_TIME_MARKS.get();
    }

    public static void removeTime() {
        TASK_TIME_MARKS.remove();
    }

}
