package com.littleyes.threadpool.util;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import static com.littleyes.threadpool.util.Constants.*;

/**
 * <p> <b> 线程池创建参数 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-19
 */
@Data
@RequiredArgsConstructor
@Accessors(chain = true)
public class HawkEyeExecutorParam {

    @NonNull
    private String name;

    private int corePoolSize = DEFAULT_CORE_POOL_SIZE;
    private int maximumPoolSize = Integer.MAX_VALUE;
    private long keepAliveSeconds = DEFAULT_KEEP_ALIVE_SECONDS;
    private int queueCapacity = Integer.MAX_VALUE;

    private boolean preStartAllCoreThreads = false;
    private boolean allowCoreThreadTimeOut = false;

    private boolean daemon = false;
    private boolean discard = true;
    private long threadRenewalDelay = DEFAULT_THREAD_RENEWAL_DELAY;
    private double memoryThresholdRate = DEFAULT_MEMORY_THRESHOLD_RATE;

}
