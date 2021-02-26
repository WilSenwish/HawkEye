package com.littleyes.agent.core.jvm.gc;

import com.littleyes.collector.dto.jvm.GarbageCollectorMetric;
import com.littleyes.threadpool.util.HawkEyeExecutors;

import java.security.SecureRandom;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class TestGarbageCollectorMetricProvider {
    public static void main(String[] args) throws InterruptedException {
        System.out.println(Long.MAX_VALUE + 1);
        System.out.println(Long.MAX_VALUE + 2);
        System.out.println((Long.MAX_VALUE + 2) - (Long.MAX_VALUE + 1));

        GarbageCollectorMetricProvider.getGarbageCollectorMetricList();

        List<GarbageCollectorMetric> data = new CopyOnWriteArrayList<>();
        ExecutorService executorService = HawkEyeExecutors.newThreadExecutor("test");
        while (true) {
            executorService.execute(() -> {
                List<GarbageCollectorMetric> gcList = GarbageCollectorMetricProvider.getGarbageCollectorMetricList();
                for (int i = 0; i < 1000; i++) {
                    data.addAll(gcList);
                }
                gcList.forEach(System.out::println);
            });
            TimeUnit.MILLISECONDS.sleep(new SecureRandom().nextInt(500));
        }
    }
}
