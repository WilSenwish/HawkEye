package com.littleyes.threadpool.util;

import com.littleyes.threadpool.core.HawkEyeThreadPoolExecutor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.ForkJoinPool;

/**
 * <p> <b> 线程池状态 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-19
 */
@Data
@NoArgsConstructor
public class ThreadPoolExecutorStat {

    /**
     * 名称
     */
    private String name;
    /**
     * 线程池需要执行的任务数
     */
    private long taskCount;
    /**
     * 线程池在运行过程中已完成的任务数
     */
    private long completedTaskCount;
    /**
     * 线程池里活跃的线程数量
     */
    private long activeCount;
    /**
     * 线程池里提交的任务数量
     */
    private long submittedCount;
    /**
     * 线程池里的线程数量
     */
    private long poolSize;
    /**
     * 线程池里曾经创建过的最大线程数
     */
    private long largestPoolSize;
    /**
     * 线程池里工作队列剩余任务数量
     */
    private int workQueueSize;

    /**
     * 线程池状态记录时间
     */
    private long timestamp;


    ThreadPoolExecutorStat(HawkEyeThreadPoolExecutor executor) {
        this.name = executor.getName();
        this.taskCount = executor.getTaskCount();
        this.completedTaskCount = executor.getCompletedTaskCount();
        this.activeCount = executor.getActiveCount();
        this.submittedCount = executor.getSubmittedCount();
        this.poolSize = executor.getPoolSize();
        this.largestPoolSize = executor.getLargestPoolSize();
        this.workQueueSize = executor.getQueue().size();

        this.timestamp = System.currentTimeMillis();
    }

    ThreadPoolExecutorStat(ForkJoinPool pool, String name) {
        this.name = name;
        this.activeCount = pool.getActiveThreadCount();
        this.submittedCount = pool.getQueuedSubmissionCount();
        this.poolSize = pool.getPoolSize();
        this.largestPoolSize = pool.getPoolSize();
        this.workQueueSize = (int) pool.getQueuedTaskCount();

        this.timestamp = System.currentTimeMillis();
    }

}
