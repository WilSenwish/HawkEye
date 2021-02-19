package com.littleyes.threadpool.exception;

import com.littleyes.threadpool.core.HawkEyeThreadPoolExecutor;

/**
 * A custom {@link RuntimeException} thrown by the {@link HawkEyeThreadPoolExecutor}
 * to signal that the thread should be disposed of.
 *
 * @author Junbing.Chen
 * @date 2021-02-19
 */
public class StoppingThreadPoolException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public StoppingThreadPoolException(String message) {
        super(message);
    }

}
