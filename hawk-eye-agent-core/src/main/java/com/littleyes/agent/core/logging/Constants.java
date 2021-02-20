package com.littleyes.agent.core.logging;

/**
 * <p> <b> 常量 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-19
 */
public interface Constants {

    String HAWK_EYE_AGENT = "|HawkEyeAgent ===> |";

    /**
     * Appender 缓存队列最大容量：{@value}
     */
    int BUFFER_QUEUE_MAX_CAPACITY = 1 << 14;

    /**
     * 监控日志分隔符：{@value}
     */
    String MONITOR_SEPARATOR = "@^@";

}
