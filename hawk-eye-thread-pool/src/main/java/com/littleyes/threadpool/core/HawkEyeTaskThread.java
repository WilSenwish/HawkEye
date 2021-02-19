package com.littleyes.threadpool.core;

import com.littleyes.threadpool.exception.StopPooledThreadException;
import lombok.extern.slf4j.Slf4j;

import static com.littleyes.threadpool.util.Constants.NAME;

/**
 * A Thread implementation that records the time at which it was created.
 *
 * @author Junbing.Chen
 * @date 2021-02-19
 */
@Slf4j
class HawkEyeTaskThread extends Thread {

    private final long creationTime;

    HawkEyeTaskThread(ThreadGroup group, Runnable target, String name, long stackSize) {
        super(group, new WrappedRunnable(target), name, stackSize);
        this.creationTime = System.currentTimeMillis();
    }

    /**
     * @return the time (in ms) at which this thread was created
     */
    final long getCreationTime() {
        return creationTime;
    }

    /**
     * Wraps a {@link Runnable} to swallow any {@link StopPooledThreadException}
     * instead of letting it go and potentially trigger a break in a debugger.
     */
    private static class WrappedRunnable implements Runnable {

        private Runnable wrappedRunnable;

        WrappedRunnable(Runnable wrappedRunnable) {
            this.wrappedRunnable = wrappedRunnable;
        }

        @Override
        public void run() {
            try {
                wrappedRunnable.run();
            } catch (StopPooledThreadException exc) {
                log.error("{} Thread exiting on purpose", NAME, exc);
            } catch (Exception e) {
                log.error("{} Thread[{}] task[{}] execute with error: {}",
                        NAME, Thread.currentThread().getName(), wrappedRunnable.toString(), e);
            }
        }

    }

}
