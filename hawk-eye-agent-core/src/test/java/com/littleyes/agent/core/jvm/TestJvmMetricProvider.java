package com.littleyes.agent.core.jvm;

import com.littleyes.common.dto.jvm.JvmMetric;
import com.littleyes.common.util.JsonUtils;

import java.security.SecureRandom;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public class TestJvmMetricProvider {
    public static void main(String[] args) throws InterruptedException {
        ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();

        List<JvmMetric> data = new CopyOnWriteArrayList<>();
        while (true) {
            forkJoinPool.execute(() -> {
                JvmMetric jvmMetric = JvmMetricProvider.getJvmMetric();
                for (int i = 0; i < 500; i++) {
                    data.add(jvmMetric);
                }
                System.out.println(JsonUtils.toString(jvmMetric));
            });
            TimeUnit.MILLISECONDS.sleep(new SecureRandom().nextInt(500));
        }
    }
}
