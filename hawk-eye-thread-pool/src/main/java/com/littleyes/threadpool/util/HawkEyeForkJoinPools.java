package com.littleyes.threadpool.util;

import com.littleyes.common.ep.PluginLoader;
import com.littleyes.common.util.SystemRuntime;
import com.littleyes.threadpool.core.HawkEyeThreadFactory;
import com.littleyes.threadpool.spi.ThreadPoolDelivery;
import lombok.extern.slf4j.Slf4j;

import java.security.SecureRandom;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static com.littleyes.threadpool.util.Constants.MILLIS_PER_MINUTE;
import static com.littleyes.threadpool.util.Constants.HAWK_EYE_POOL;

/**
 * <p> <b> ForkJoinPool 拓展工具类 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-19
 */
@Slf4j
public class HawkEyeForkJoinPools {

    private HawkEyeForkJoinPools() {
    }

    static {
        DefaultForkJoinPoolsBuilder
                .monitorForkJoinPool(ForkJoinPool.commonPool(), "ForkJoinPool.commonPool");
    }

    public static void monitorForkJoinPoolOfCommonPool() {
    }

    /**
     * Creates a thread pool that maintains enough threads to support
     * the given parallelism level, and may use multiple queues to
     * reduce contention. The parallelism level corresponds to the
     * maximum number of threads actively engaged in, or available to
     * engage in, task processing. The actual number of threads may
     * grow and shrink dynamically. A work-stealing pool makes no
     * guarantees about the order in which submitted tasks are
     * executed.
     *
     * @param parallelism the targeted parallelism level
     * @return the newly created thread pool
     * @throws IllegalArgumentException if {@code parallelism <= 0}
     * @since 1.8
     */
    public static ForkJoinPool newForkJoinPool(int parallelism) {
        return newForkJoinPool(
                parallelism,
                ForkJoinPool.defaultForkJoinWorkerThreadFactory,
                null,
                true
        );
    }

    /**
     * Creates a work-stealing thread pool using all
     * {@link Runtime#availableProcessors available processors}
     * as its target parallelism level.
     * @return the newly created thread pool
     * @see #newForkJoinPool(int)
     * @since 1.8
     */
    public static ForkJoinPool newForkJoinPool(boolean asyncMode) {
        return newForkJoinPool(
                SystemRuntime.CPU_CORES,
                ForkJoinPool.defaultForkJoinWorkerThreadFactory,
                null,
                asyncMode
        );
    }

    public static ForkJoinPool newForkJoinPool(int parallelism,
                                               ForkJoinPool.ForkJoinWorkerThreadFactory factory,
                                               Thread.UncaughtExceptionHandler handler,
                                               boolean asyncMode) {

        return DefaultForkJoinPoolsBuilder.newIt()
                .withParallelism(parallelism)
                .withFactory(factory)
                .withHandler(handler)
                .withAsyncMode(asyncMode)
                .build();
    }

    private static final class DefaultForkJoinPoolsBuilder {
        private static AtomicInteger poolNumberSequence = new AtomicInteger();

        private String name;

        private int parallelism = SystemRuntime.CPU_CORES;
        private ForkJoinPool.ForkJoinWorkerThreadFactory factory = ForkJoinPool.defaultForkJoinWorkerThreadFactory;
        private Thread.UncaughtExceptionHandler handler;
        private boolean asyncMode = true;

        private DefaultForkJoinPoolsBuilder(String name) {
            this.name = name;
        }

        static DefaultForkJoinPoolsBuilder newIt() {
            return new DefaultForkJoinPoolsBuilder("ForkJoinPool-" + poolNumberSequence.incrementAndGet());
        }

        DefaultForkJoinPoolsBuilder withParallelism(int parallelism) {
            this.parallelism = parallelism;
            return this;
        }

        DefaultForkJoinPoolsBuilder withFactory(ForkJoinPool.ForkJoinWorkerThreadFactory factory) {
            this.factory = factory;
            return this;
        }

        DefaultForkJoinPoolsBuilder withHandler(Thread.UncaughtExceptionHandler handler) {
            this.handler = handler;
            return this;
        }

        DefaultForkJoinPoolsBuilder withAsyncMode(boolean asyncMode) {
            this.asyncMode = asyncMode;
            return this;
        }

        ForkJoinPool build() {
            ForkJoinPool pool = new ForkJoinPool(parallelism, factory, handler, asyncMode);
            monitorForkJoinPool(pool, name);
            return pool;
        }

        static void monitorForkJoinPool(ForkJoinPool pool, String name) {
            MonitorRunnable runnable = new MonitorRunnable(pool, name);
            long randomDelay = new SecureRandom().nextInt(6) * (MILLIS_PER_MINUTE / 6);
            long initialDelay = (MILLIS_PER_MINUTE - (System.currentTimeMillis() % MILLIS_PER_MINUTE)) + randomDelay;

            Executors
                    .newSingleThreadScheduledExecutor(new HawkEyeThreadFactory(name + ".Monitor", true))
                    .scheduleAtFixedRate(runnable, initialDelay, MILLIS_PER_MINUTE, TimeUnit.MILLISECONDS);

            log.info("{} Initialized [NO.{}] ForkJoinPool[{}]!", HAWK_EYE_POOL, (poolNumberSequence.get() + 1), name);
        }

    }

    @Slf4j
    private static class MonitorRunnable implements Runnable {

        private final AtomicLong counter = new AtomicLong();

        private final ForkJoinPool pool;
        private final ThreadPoolExecutorInfo info;

        MonitorRunnable(ForkJoinPool pool, String name) {
            this.pool = pool;
            this.info = new ThreadPoolExecutorInfo(pool, name);
        }

        @Override
        public void run() {
            try {
                if (ThreadPoolExecutorInfo.needThreadPoolExecutorInfo(counter)) {
                    PluginLoader.of(ThreadPoolDelivery.class).load().deliverThreadPoolExecutorInfo(info);
                }

                PluginLoader.of(ThreadPoolDelivery.class).load()
                        .deliverThreadPoolExecutorStat(new ThreadPoolExecutorStat(pool, info.getName()));
            } finally {
                counter.incrementAndGet();
            }
        }

    }

}
