package com.littleyes.threadpool;

import com.littleyes.threadpool.util.HawkEyeForkJoinPools;
import com.littleyes.threadpool.util.HawkEyeExecutors;

import java.security.SecureRandom;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

/**
 * <p> <b> // TODO </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-19
 */
public class TestThreadPool {
    public static void main(String[] args) throws InterruptedException {
        HawkEyeForkJoinPools.monitorForkJoinPoolOfCommonPool();
        for (int i = 0; i < 10; i++) {
            ForkJoinPool.commonPool().execute(() -> System.out.println(new SecureRandom().nextDouble()));
        }

        ExecutorService executorService = HawkEyeExecutors.newThreadExecutor("test", 4);
        for (int i = 0; i < 10; i++) {
            executorService.execute(() -> System.out.println(new SecureRandom().nextDouble()));
            TimeUnit.MILLISECONDS.sleep(new SecureRandom().nextInt(100));
        }
    }
}
