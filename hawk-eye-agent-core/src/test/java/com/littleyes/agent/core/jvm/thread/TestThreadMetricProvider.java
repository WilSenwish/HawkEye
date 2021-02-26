package com.littleyes.agent.core.jvm.thread;

import com.littleyes.threadpool.util.HawkEyeExecutors;

import java.security.SecureRandom;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class TestThreadMetricProvider {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = HawkEyeExecutors.newThreadExecutor("test");
        while (true) {
            executorService.execute(() -> {
                System.out.println(ThreadMetricProvider.getThreadMetric());
                try {
                    TimeUnit.MILLISECONDS.sleep(new SecureRandom().nextInt(1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            TimeUnit.MILLISECONDS.sleep(new SecureRandom().nextInt(200));
        }
    }
}
