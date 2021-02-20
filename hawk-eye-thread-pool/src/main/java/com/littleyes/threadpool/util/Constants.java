package com.littleyes.threadpool.util;

/**
 * <p> <b> 线程池常量 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-19
 */
public interface Constants {

    String HAWK_EYE_POOL = "@HawkEyePool ===> |";

    /**
     * default corePoolSize: {@value}
     */
    int DEFAULT_CORE_POOL_SIZE = 1;

    /**
     * minute's millis: {@value}
     */
    long MILLIS_PER_MINUTE = 1000L * 60L;

    /**
     * default threadRenewalDelay: {@value}
     */
    long DEFAULT_THREAD_RENEWAL_DELAY = 1000L;

    /**
     * default keepAliveSeconds: {@value}
     */
    long DEFAULT_KEEP_ALIVE_SECONDS = 60L;

    /**
     * default memoryThresholdRate: {@value}
     */
    double DEFAULT_MEMORY_THRESHOLD_RATE = 0.75D;

}
