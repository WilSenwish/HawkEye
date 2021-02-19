package com.littleyes.threadpool.core;

import com.littleyes.threadpool.exception.StopPooledThreadException;
import com.littleyes.threadpool.util.MarkerContext;
import lombok.extern.slf4j.Slf4j;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

import static com.littleyes.threadpool.util.Constants.*;

/**
 * Same as a java.util.concurrent.ThreadPoolExecutor but implements a much more efficient
 * {@link #getSubmittedCount()} method, to be used to properly handle the work queue.
 * If a RejectedExecutionHandler is not specified a default one will be configured
 * and that one will always throw a RejectedExecutionException
 *
 * @author Junbing.Chen
 * @date 2021-02-19
 */
@Slf4j
public class HawkEyeThreadPoolExecutor extends ThreadPoolExecutor {

    /**
     * The number of tasks submitted but not yet finished. This includes tasks
     * in the queue and tasks that have been handed to a worker thread but the
     * latter did not start executing the task yet.
     * This number is always greater or equal to {@link #getActiveCount()}.
     */
    private final AtomicLong submittedCount = new AtomicLong(0L);
    private final AtomicLong lastContextStoppedTime = new AtomicLong(0L);

    /**
     * Most recent time in ms when a thread decided to kill itself to avoid
     * potential memory leaks. Useful to throttle the rate of renewals of
     * threads.
     */
    private final AtomicLong lastTimeThreadKilledItself = new AtomicLong(0L);

    /**
     * Delay in ms between 2 threads being renewed. If negative, do not renew threads.
     */
    private long threadRenewalDelay = DEFAULT_THREAD_RENEWAL_DELAY;

    /**
     * Application memory threshold rate for task execute check
     */
    private double memoryThresholdRate = DEFAULT_MEMORY_THRESHOLD_RATE;

    private String name;

    public HawkEyeThreadPoolExecutor(String name,
                                         int corePoolSize,
                                         int maximumPoolSize,
                                         long keepAliveTime,
                                         TimeUnit unit,
                                         BlockingQueue<Runnable> workQueue,
                                         ThreadFactory threadFactory,
                                         RejectedExecutionHandler handler) {

        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        this.name = name;
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        MarkerContext.markIfEmpty(t.getName());
        MarkerContext.begin();

        super.beforeExecute(t, r);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(Runnable command) {
        execute(command, 0, TimeUnit.MILLISECONDS);
    }

    /**
     * Executes the given command at some time in the future.  The command
     * may execute in a new thread, in a pooled thread, or in the calling
     * thread, at the discretion of the <tt>Executor</tt> implementation.
     * If no threads are available, it will be added to the work queue.
     * If the work queue is full, the system will wait for the specified
     * time and it throw a RejectedExecutionException if the queue is still
     * full after that.
     *
     * @param command the runnable task
     * @param timeout A timeout for the completion of the task
     * @param unit    The timeout time unit
     * @throws RejectedExecutionException if this task cannot be
     *                                    accepted for execution - the queue is full
     * @throws NullPointerException       if command or unit is null
     */
    private void execute(Runnable command, long timeout, TimeUnit unit) {
        submittedCount.incrementAndGet();
        try {
            super.execute(command);
        } catch (RejectedExecutionException rx) {
            if (super.getQueue() instanceof HawkEyeTaskQueue) {
                final HawkEyeTaskQueue queue = (HawkEyeTaskQueue) super.getQueue();
                try {
                    if (!queue.force(command, timeout, unit)) {
                        submittedCount.decrementAndGet();
                        throw new RejectedExecutionException("Queue capacity is full!");
                    }
                } catch (InterruptedException x) {
                    submittedCount.decrementAndGet();
                    throw new RejectedExecutionException(x);
                }
            } else {
                submittedCount.decrementAndGet();
                throw rx;
            }

        }
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        submittedCount.decrementAndGet();

        try {
            long timeConsume = System.currentTimeMillis() - MarkerContext.getBeginTime();
            if (timeConsume > MILLIS_PER_MINUTE) {
                log.warn("{} Task[{}] execute with time consume [{}]ms", HAWK_EYE_POOL, MarkerContext.getMarker(), timeConsume);
            }
        } finally {
            // Mark in the thread task
            MarkerContext.removeMark();
            MarkerContext.removeTime();
        }

        if (Objects.isNull(t)) {
            stopCurrentThreadIfNeeded();
        }
    }

    @Override
    protected void terminated() {
        super.terminated();
        log.warn("{} HawkEyeThreadPoolExecutor[{}] terminated!!!", HAWK_EYE_POOL, name);
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        return new FutureTask<>(callable);
    }

    public long getSubmittedCount() {
        return submittedCount.get();
    }

    public String getName() {
        return name;
    }

    public long getThreadRenewalDelay() {
        return threadRenewalDelay;
    }

    public void setThreadRenewalDelay(long threadRenewalDelay) {
        this.threadRenewalDelay = threadRenewalDelay;
    }

    public double getMemoryThresholdRate() {
        return memoryThresholdRate;
    }

    public void setMemoryThresholdRate(double memoryThresholdRate) {
        this.memoryThresholdRate = memoryThresholdRate;
    }

    /**
     * If the current thread was started before the last time when a context was
     * stopped, an exception is thrown so that the current thread is stopped.
     */
    void stopCurrentThreadIfNeeded() {
        if (currentThreadShouldBeStopped()) {
            long lastTime = lastTimeThreadKilledItself.longValue();
            if (lastTime + threadRenewalDelay < System.currentTimeMillis()) {
                if (lastTimeThreadKilledItself.compareAndSet(lastTime,
                        System.currentTimeMillis() + 1)) {
                    // OK, it's really time to dispose of this thread

                    final String msg = "Stopping thread ["
                            + Thread.currentThread().getName()
                            + "] to avoid potential memory leaks after a context was stopped!";

                    throw new StopPooledThreadException(msg);
                }
            }
        }
    }

    boolean currentThreadShouldBeStopped() {
        if (threadRenewalDelay >= 0 && Thread.currentThread() instanceof HawkEyeTaskThread) {
            HawkEyeTaskThread currentTaskThread = (HawkEyeTaskThread) Thread.currentThread();
            return currentTaskThread.getCreationTime() < this.lastContextStoppedTime.longValue();
        }

        return false;
    }

    public void contextStopping() {
        this.lastContextStoppedTime.set(System.currentTimeMillis());

        // save the current pool parameters to restore them later
        int savedCorePoolSize = this.getCorePoolSize();
        HawkEyeTaskQueue taskQueue =
                getQueue() instanceof HawkEyeTaskQueue ? (HawkEyeTaskQueue) getQueue() : null;
        if (Objects.nonNull(taskQueue)) {
            // note by slaurent : quite oddly threadPoolExecutor.setCorePoolSize
            // checks that queue.remainingCapacity()==0. I did not understand
            // why, but to get the intended effect of waking up idle threads, I
            // temporarily fake this condition.
            taskQueue.setForcedRemainingCapacity(0);
        }

        // setCorePoolSize(0) wakes idle threads
        this.setCorePoolSize(0);

        // TaskQueue.take() takes care of timing out, so that we are sure that
        // all threads of the pool are renewed in a limited time, something like
        // (threadKeepAlive + longest request time)

        if (Objects.nonNull(taskQueue)) {
            // ok, restore the state of the queue and pool
            taskQueue.setForcedRemainingCapacity(null);
        }
        this.setCorePoolSize(savedCorePoolSize);
    }

    boolean overMemoryThreshold() {
        MemoryUsage memoryUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        long thresholdSize = (long) (memoryUsage.getMax() * getMemoryThresholdRate());

        return memoryUsage.getUsed() > thresholdSize;
    }

    public static class DefaultRetryThenRejectHandler implements RejectedExecutionHandler {

        private boolean discard = true;

        public DefaultRetryThenRejectHandler() {
        }

        public DefaultRetryThenRejectHandler(boolean discard) {
            this.discard = discard;
        }

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            if (canRetry(executor)) {
                try {
                    boolean reAdded;
                    BlockingQueue queue = executor.getQueue();

                    if (queue instanceof HawkEyeTaskQueue) {
                        reAdded = ((HawkEyeTaskQueue) queue).force(r);
                    } else {
                        reAdded = executor.getQueue().offer(r);
                    }

                    if (reAdded) {
                        return;
                    }
                } catch (Throwable e) {
                    throw new RejectedExecutionException(e);
                }
            }

            throw new RejectedExecutionException();
        }

        private boolean canRetry(ThreadPoolExecutor executor) {
            return
                    !this.discard
                            || (executor instanceof HawkEyeThreadPoolExecutor
                            && !((HawkEyeThreadPoolExecutor) executor).overMemoryThreshold());
        }

    }

}
