package com.littleyes.threadpool.core;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple named task thread factory to use to create threads for an executor
 * implementation.
 *
 * @author Junbing.Chen
 * @date 2021-02-19
 */
@Slf4j
public class HawkEyeThreadFactory implements ThreadFactory {

    private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);

    private final AtomicInteger threadNumber = new AtomicInteger(1);

    private final ThreadGroup group;
    private final String namePrefix;
    private boolean daemon;

    public HawkEyeThreadFactory(String namePrefix) {
        SecurityManager s = System.getSecurityManager();
        group = Objects.nonNull(s) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        this.namePrefix = "Pool-" + POOL_NUMBER.getAndIncrement() + "." + namePrefix + "-Thread-";
    }

    public HawkEyeThreadFactory(String namePrefix, boolean daemon) {
        this(namePrefix);
        this.daemon = daemon;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new HawkEyeTaskThread(group, r, (namePrefix + threadNumber.getAndIncrement()), 0);
        t.setDaemon(daemon);

        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }

        return t;
    }

}
