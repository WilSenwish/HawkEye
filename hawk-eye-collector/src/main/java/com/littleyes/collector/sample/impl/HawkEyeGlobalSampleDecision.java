package com.littleyes.collector.sample.impl;

import com.littleyes.collector.sample.SampleDecision;
import com.littleyes.collector.sample.SampleDecisionChain;
import com.littleyes.common.trace.TraceContext;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p> <b> 全局兜底采样决策 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-03-19
 */
public class HawkEyeGlobalSampleDecision implements SampleDecision {

    public static final int DEFAULT_SAMPLE_RATE_BASE = 100;

    private boolean globalSampleEnabled;
    private boolean fullSampleRate;

    private int globalSampleRateBase;
    private AtomicInteger globalSampleDataIndex = new AtomicInteger();

    private final Set<Integer> globalSampleIndexes = new HashSet<>(DEFAULT_SAMPLE_RATE_BASE);

    @Override
    public int order() {
        return 99;
    }

    @Override
    public void init(Properties config) {
        int globalSampleRate        = Integer.parseInt(config.getProperty("globalSampleRate", "20"));
        this.globalSampleEnabled    = globalSampleRate > 0;
        if (this.globalSampleEnabled) {
            initGlobalSampleConfigParams(globalSampleRate);
        }
    }

    @Override
    public boolean decide(TraceContext context, SampleDecisionChain chain) {
        // 符合全部或部分采样率则采集
        if (this.globalSampleEnabled) {
           if (this.fullSampleRate || needSample()) {
               return true;
           }
        }

        return chain.decide(context);
    }

    private boolean needSample() {
        int currentSampleIndex = (this.globalSampleDataIndex.getAndIncrement() % this.globalSampleRateBase);
        return this.globalSampleIndexes.contains(currentSampleIndex);
    }

    private void initGlobalSampleConfigParams(int globalSampleRate) {
        globalSampleRate = extractConfiguredGlobalSampleRate(globalSampleRate);
        this.fullSampleRate = (DEFAULT_SAMPLE_RATE_BASE == globalSampleRate);

        if (!this.fullSampleRate) {
            if (DEFAULT_SAMPLE_RATE_BASE % globalSampleRate == 0) {
                this.globalSampleRateBase = DEFAULT_SAMPLE_RATE_BASE / globalSampleRate;
                globalSampleRate = 1;
            } else {
                this.globalSampleRateBase = DEFAULT_SAMPLE_RATE_BASE;
            }

            SecureRandom random = new SecureRandom();
            while (this.globalSampleIndexes.size() < globalSampleRate) {
                this.globalSampleIndexes.add(random.nextInt(this.globalSampleRateBase));
            }
        }
    }

    private int extractConfiguredGlobalSampleRate(int globalSampleRate) {
        return globalSampleRate > DEFAULT_SAMPLE_RATE_BASE ? DEFAULT_SAMPLE_RATE_BASE : globalSampleRate;
    }

}
