package com.littleyes.threadpool.util;

import com.littleyes.common.ep.EpLoader;
import com.littleyes.common.util.SystemRuntime;
import com.littleyes.threadpool.core.HawkEyeTaskQueue;
import com.littleyes.threadpool.core.HawkEyeThreadFactory;
import com.littleyes.threadpool.core.HawkEyeThreadPoolExecutor;
import com.littleyes.threadpool.spi.ThreadPoolDelivery;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static com.littleyes.threadpool.util.Constants.*;

/**
 * <p> <b> 线程池工具类 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-19
 */
@Slf4j
@Getter
public class HawkEyeExecutors {

    private HawkEyeExecutors() {
    }

    public static ThreadPoolExecutor newThreadExecutor(String name) {
        return Builder.of(name).build();
    }

    public static ThreadPoolExecutor newThreadExecutor(String name, int maximumPoolSize) {
        return Builder.of(name)
                .withMaximumPoolSize(maximumPoolSize)
                .build();
    }

    public static ThreadPoolExecutor newThreadExecutor(String name, int maximumPoolSize, int workQueueCapacity) {
        return Builder.of(name)
                .withMaximumPoolSize(maximumPoolSize)
                .withQueueCapacity(workQueueCapacity)
                .build();
    }

    public static ThreadPoolExecutor newThreadExecutor(HawkEyeExecutorParam param) {
        return Builder.of(param.getName())
                .withCorePoolSize(param.getCorePoolSize())
                .withMaximumPoolSize(param.getMaximumPoolSize())
                .withKeepAliveSeconds(param.getKeepAliveSeconds())
                .withQueueCapacity(param.getQueueCapacity())
                .withPreStartAllCoreThreads(param.isPreStartAllCoreThreads())
                .withAllowCoreThreadTimeOut(param.isAllowCoreThreadTimeOut())
                .withDaemon(param.isDaemon())
                .withDiscard(param.isDiscard())
                .withThreadRenewalDelay(param.getThreadRenewalDelay())
                .withMemoryThresholdRate(param.getMemoryThresholdRate())
                .build();
    }

    private static final class Builder {

        private static final AtomicInteger MONITOR_COUNTER = new AtomicInteger();

        private static final ConcurrentMap<String, HawkEyeThreadPoolExecutor> RUNNING_EXECUTORS
                = new ConcurrentHashMap<>();

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

        static {
            SystemRuntime.addShutdownHook(
                    new Thread(
                            () -> {
                                log.info("{} All HawkEyeThreadPoolExecutor size: {}", NAME, RUNNING_EXECUTORS.size());

                                RUNNING_EXECUTORS.values().parallelStream().forEach(executor -> {
                                    try {
                                        if (!executor.isShutdown()) {
                                            executor.shutdown();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                });
                                RUNNING_EXECUTORS.clear();

                                log.info("{} All HawkEyeThreadPoolExecutor have been shutdown!!!", NAME);
                            },
                            "HawkEyeThreadPoolExecutorShutdownHook"
                    )
            );
        }

        private Builder(String name) {
            if (Objects.isNull(name)) {
                throw new NullPointerException("ThreadPoolExecutor's name cannot be null!");
            }

            this.name = name;
        }

        static Builder of(String name) {
            return new Builder(name);
        }

        Builder withCorePoolSize(int corePoolSize) {
            if (corePoolSize < 1) {
                throw new IllegalArgumentException("ThreadPoolExecutor's corePoolSize must gte 1!");
            }

            this.corePoolSize = corePoolSize;
            return this;
        }

        Builder withMaximumPoolSize(int maximumPoolSize) {
            if (maximumPoolSize < 1) {
                throw new IllegalArgumentException("ThreadPoolExecutor's maximumPoolSize must gte 1!");
            }

            this.maximumPoolSize = maximumPoolSize;
            return this;
        }

        Builder withKeepAliveSeconds(long keepAliveSeconds) {
            if (keepAliveSeconds < 0L) {
                throw new IllegalArgumentException("ThreadPoolExecutor's keepAliveSeconds must gte 0!");
            }

            this.keepAliveSeconds = keepAliveSeconds;
            return this;
        }

        Builder withQueueCapacity(int queueCapacity) {
            if (queueCapacity < 0) {
                throw new IllegalArgumentException("ThreadPoolExecutor's queueCapacity must gte 0!");
            }

            this.queueCapacity = queueCapacity;
            return this;
        }

        Builder withPreStartAllCoreThreads(boolean preStartAllCoreThreads) {
            this.preStartAllCoreThreads = preStartAllCoreThreads;
            return this;
        }

        Builder withAllowCoreThreadTimeOut(boolean allowCoreThreadTimeOut) {
            this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
            return this;
        }

        Builder withDaemon(boolean daemon) {
            this.daemon = daemon;
            return this;
        }

        Builder withDiscard(boolean discard) {
            this.discard = discard;
            return this;
        }

        Builder withThreadRenewalDelay(long threadRenewalDelay) {
            this.threadRenewalDelay = threadRenewalDelay;
            return this;
        }

        Builder withMemoryThresholdRate(double memoryThresholdRate) {
            if (memoryThresholdRate >= BigDecimal.ONE.doubleValue()) {
                throw new IllegalArgumentException("ThreadPoolExecutor's memoryThresholdRate must lt 1!");
            }

            this.memoryThresholdRate = memoryThresholdRate;
            return this;
        }

        HawkEyeThreadPoolExecutor build() {
            HawkEyeThreadPoolExecutor runningExecutor = RUNNING_EXECUTORS.get(name);
            if (Objects.nonNull(runningExecutor)) {
                log.warn("{} Reuse HawkEyeThreadPoolExecutor[{}]!", NAME, name);
                return runningExecutor;
            }

            BlockingQueue<Runnable> workQueue = createWorkQueue();

            HawkEyeThreadPoolExecutor executor = new HawkEyeThreadPoolExecutor(
                    name,
                    corePoolSize,
                    maximumPoolSize,
                    keepAliveSeconds,
                    TimeUnit.SECONDS,
                    workQueue,
                    new HawkEyeThreadFactory(name, daemon),
                    new HawkEyeThreadPoolExecutor.DefaultRetryThenRejectHandler(discard)
            );

            executor.setThreadRenewalDelay(threadRenewalDelay);
            executor.setMemoryThresholdRate(memoryThresholdRate);

            if (this.allowCoreThreadTimeOut) {
                executor.allowCoreThreadTimeOut(true);
            }

            if (this.preStartAllCoreThreads) {
                executor.prestartAllCoreThreads();
            }

            if (workQueue instanceof HawkEyeTaskQueue) {
                ((HawkEyeTaskQueue) workQueue).setParent(executor);
            }

            if (Objects.nonNull(RUNNING_EXECUTORS.putIfAbsent(name, executor))) {
                throw new IllegalArgumentException("HawkEyeThreadPoolExecutor[" + name + "] already exists!");
            }

            monitorThreadPoolExecutor(executor);

            return executor;
        }

        /**
         * Create the BlockingQueue to use for the ThreadPoolExecutor.
         * <p>A LinkedBlockingQueue instance will be created for a positive
         * capacity value; a SynchronousQueue else.
         *
         * @return the BlockingQueue instance
         * @see LinkedBlockingQueue
         * @see SynchronousQueue
         */
        private BlockingQueue<Runnable> createWorkQueue() {
            if (this.queueCapacity > 0) {
                return new HawkEyeTaskQueue(this.queueCapacity, this.discard);
            } else {
                return new SynchronousQueue<>();
            }
        }

        private void monitorThreadPoolExecutor(HawkEyeThreadPoolExecutor executor) {
            MonitorRunnable runnable = new MonitorRunnable(executor, queueCapacity, preStartAllCoreThreads);
            long currentThreadPoolCount = MONITOR_COUNTER.incrementAndGet();
            long randomDelay = (currentThreadPoolCount % 60) * (MILLIS_PER_MINUTE / 60);
            long initialDelay = (MILLIS_PER_MINUTE - (System.currentTimeMillis() % MILLIS_PER_MINUTE)) + randomDelay;

            Executors
                    .newSingleThreadScheduledExecutor(new HawkEyeThreadFactory((name + ".Monitor"), true))
                    .scheduleAtFixedRate(runnable, initialDelay, MILLIS_PER_MINUTE, TimeUnit.MILLISECONDS);

            log.info("{} Initialized [NO.{}] HawkEyeThreadPoolExecutor[{}]!", NAME, currentThreadPoolCount, name);
        }

        private static class MonitorRunnable implements Runnable {

            private final AtomicLong counter = new AtomicLong();

            private final HawkEyeThreadPoolExecutor executor;
            private final ThreadPoolExecutorInfo info;

            MonitorRunnable(HawkEyeThreadPoolExecutor executor,
                            int queueCapacity,
                            boolean preStartAllCoreThreads) {

                this.executor = executor;
                this.info = new ThreadPoolExecutorInfo(executor, queueCapacity, preStartAllCoreThreads);
            }

            @Override
            public void run() {
                try {
                    if (ThreadPoolExecutorInfo.needThreadPoolExecutorInfo(counter)) {
                        EpLoader.of(ThreadPoolDelivery.class).load().deliverThreadPoolExecutorInfo(info);
                    }

                    EpLoader.of(ThreadPoolDelivery.class).load()
                            .deliverThreadPoolExecutorStat(new ThreadPoolExecutorStat(executor));
                } finally {
                    counter.incrementAndGet();
                }
            }

        }

    }

}
