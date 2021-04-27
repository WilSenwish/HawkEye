package com.littleyes.threadpool.exception;

import com.littleyes.threadpool.core.HawkEyeThreadPoolExecutor;

/**
 * A custom {@link RuntimeException} thrown by the {@link HawkEyeThreadPoolExecutor}
 * to signal that the thread should be disposed of.
 *
 * @author Junbing.Chen
 * @date 2021-02-19
 */
public class StopPooledThreadException extends RuntimeException {

    private static final long serialVersionUID = 147L;

    public StopPooledThreadException(String message) {
        super(message);
    }

}
