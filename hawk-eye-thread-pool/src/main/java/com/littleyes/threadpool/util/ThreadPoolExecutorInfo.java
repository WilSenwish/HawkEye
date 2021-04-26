package com.littleyes.threadpool.util;

import com.littleyes.threadpool.core.HawkEyeThreadPoolExecutor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <p> <b> 线程池状态 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-19
 */
@Data
@NoArgsConstructor
public class ThreadPoolExecutorInfo {

    /**
     * 名称
     */
    private String name;
    /**
     * 线程池里核心的线程数量
     */
    private long corePoolSize;
    /**
     * 线程池里最大允许线程数
     */
    private long maximumPoolSize;
    /**
     * 线程池里非核心线程空闲时的存活时间
     */
    private long keepAliveMillis;
    /**
     * 线程池里工作队列容量
     */
    private int workQueueCapacity;
    /**
     * 线程池线程工厂
     */
    private String threadFactory;
    /**
     * 线程池里不可执行任务处理策略
     */
    private String rejectedExecutionHandler;
    /**
     * Thread Renewal Delay
     */
    private long threadRenewalDelay;
    /**
     * Application memory threshold rate for task execute check
     */
    private double memoryThresholdRate;
    /**
     * 线程池是否提前启动所有核心线程
     */
    private boolean preStartAllCoreThreads;
    /**
     * 线程池是否允许核心线程超时
     */
    private boolean allowCoreThreadTimeOut;
    /**
     * 线程池是否关闭
     */
    private boolean shutdown;
    /**
     * 线程池是否终止
     */
    private boolean terminated;

    /**
     * 线程池启动时间
     */
    private long startTime;


    ThreadPoolExecutorInfo(HawkEyeThreadPoolExecutor executor,
                           int queueCapacity, boolean preStartAllCoreThreads) {

        this.name = executor.getName();
        this.corePoolSize = executor.getCorePoolSize();
        this.maximumPoolSize = executor.getMaximumPoolSize();
        this.keepAliveMillis = executor.getKeepAliveTime(TimeUnit.MILLISECONDS);
        this.workQueueCapacity = queueCapacity;
        this.threadFactory = executor.getThreadFactory().toString();
        this.rejectedExecutionHandler = executor.getRejectedExecutionHandler().toString();
        this.preStartAllCoreThreads = preStartAllCoreThreads;
        this.allowCoreThreadTimeOut = executor.allowsCoreThreadTimeOut();
        this.threadRenewalDelay = executor.getThreadRenewalDelay();
        this.memoryThresholdRate = executor.getMemoryThresholdRate();
        this.shutdown = executor.isShutdown();
        this.terminated = executor.isTerminated();

        this.startTime = System.currentTimeMillis();
    }

    ThreadPoolExecutorInfo(ForkJoinPool pool, String name) {
        this.name = name;
        this.corePoolSize = pool.getParallelism();
        this.maximumPoolSize = pool.getParallelism();
        this.threadFactory = pool.getFactory().toString();
        this.shutdown = pool.isShutdown();
        this.terminated = pool.isTerminated();

        this.startTime = System.currentTimeMillis();
    }

    /**
     * 刚开始运行及每三十分钟收集一次
     *
     * @param counter
     * @return
     */
    public static boolean needThreadPoolExecutorInfo(AtomicLong counter) {
        long count = counter.get();

        return ((count % 30) == 0) || ((0 < count) && (count < 3));
    }

}
