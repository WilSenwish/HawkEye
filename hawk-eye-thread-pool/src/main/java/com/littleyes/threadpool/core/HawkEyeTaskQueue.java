package com.littleyes.threadpool.core;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * As task queue specifically designed to run with a thread pool executor. The
 * task queue is optimised to properly utilize threads within a thread pool
 * executor. If you use a normal queue, the executor will spawn threads when
 * there are idle threads and you wont be able to force items onto the queue
 * itself.
 *
 * @author Junbing.Chen
 * @date 2021-02-19
 */
public class HawkEyeTaskQueue extends LinkedBlockingQueue<Runnable> {

    private static final long serialVersionUID = 1L;

    private transient volatile HawkEyeThreadPoolExecutor parent = null;
    private boolean discard = true;

    /**
     * No need to be volatile. This is written and read in a single thread
     * (when stopping a context and firing the listeners)
     */
    private Integer forcedRemainingCapacity = null;

    public HawkEyeTaskQueue() {
        super();
    }

    public HawkEyeTaskQueue(int capacity) {
        super(capacity);
    }

    public HawkEyeTaskQueue(Collection<? extends Runnable> c) {
        super(c);
    }

    public HawkEyeTaskQueue(int capacity, boolean discard) {
        super(capacity);
        this.discard = discard;
    }

    public void setParent(HawkEyeThreadPoolExecutor tp) {
        parent = tp;
    }

    boolean force(Runnable o) {
        if (Objects.isNull(parent) || parent.isShutdown()) {
            throw new RejectedExecutionException("Executor not running, can't force a command into the queue!");
        }

        //forces the item onto the queue, to be used if the task is rejected
        return super.offer(o);
    }

    boolean force(Runnable o, long timeout, TimeUnit unit) throws InterruptedException {
        if (Objects.isNull(parent) || parent.isShutdown()) {
            throw new RejectedExecutionException("Executor not running, can't force a command into the queue!");
        }

        //forces the item onto the queue, to be used if the task is rejected
        return super.offer(o, timeout, unit);
    }

    @Override
    public boolean offer(Runnable o) {
        //we can't do any checks
        if (Objects.isNull(parent)) {
            return super.offer(o);
        }

        // over configured memory threshold
        if (this.discard && parent.overMemoryThreshold()) {
            return false;
        }

        //we are maxed out on threads, simply queue the object
        if (parent.getPoolSize() == parent.getMaximumPoolSize()) {
            return super.offer(o);
        }

        //we have idle threads, just add it to the queue
        if (parent.getSubmittedCount() <= parent.getPoolSize()) {
            return super.offer(o);
        }

        //if we have less threads than maximum force creation of a new thread
        if (parent.getPoolSize() < parent.getMaximumPoolSize()) {
            return false;
        }

        //if we reached here, we need to add it to the queue
        return super.offer(o);
    }

    @Override
    public Runnable poll(long timeout, TimeUnit unit) throws InterruptedException {
        Runnable runnable = super.poll(timeout, unit);
        if (Objects.isNull(runnable) && Objects.nonNull(parent)) {
            // the poll timed out, it gives an opportunity to stop the current
            // thread if needed to avoid memory leaks.
            parent.stopCurrentThreadIfNeeded();
        }

        return runnable;
    }

    @Override
    public Runnable take() throws InterruptedException {
        if (Objects.nonNull(parent) && parent.currentThreadShouldBeStopped()) {
            return poll(parent.getKeepAliveTime(TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS);
            // yes, this may return null (in case of timeout) which normally
            // does not occur with take()
            // but the ThreadPoolExecutor implementation allows this
        }

        return super.take();
    }

    @Override
    public int remainingCapacity() {
        if (Objects.nonNull(forcedRemainingCapacity)) {
            // ThreadPoolExecutor.setCorePoolSize checks that
            // remainingCapacity==0 to allow to interrupt idle threads
            // I don't see why, but this hack allows to conform to this
            // "requirement"
            return forcedRemainingCapacity;
        }

        return super.remainingCapacity();
    }

    void setForcedRemainingCapacity(Integer forcedRemainingCapacity) {
        this.forcedRemainingCapacity = forcedRemainingCapacity;
    }

}
