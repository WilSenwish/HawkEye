package com.littleyes.collector.util;

/**
 * <p> <b> 常量 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-19
 */
public interface Constants {

    String HAWK_EYE_COLLECTOR = "@HawkEyeCollector ===> |";

    /**
     * 批量提交日志数量：{@value}
     */
    int BUFFER_PROCESS_SIZE = 16;

    /**
     * Appender 缓存队列最大容量：{@value}
     */
    int BUFFER_MAX_CAPACITY = 1 << 16;

    String OPTIONS_METHOD       = "OPTIONS";
    String TRACE_ID_KEY         = "X-HAWK-EYE-TRACE-ID";
    String TRACE_DEBUG_KEY      = "X-HAWK-EYE-TRACE-DEBUG";
    String GIT_COMMIT_ID_KEY    = "X-HAWK-EYE-GIT-COMMIT-ID";

}
