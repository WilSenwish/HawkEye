package com.littleyes.collector.sample.impl;

import com.littleyes.collector.sample.SampleDecision;
import com.littleyes.collector.sample.SampleDecisionChain;
import com.littleyes.common.trace.TraceContext;

import java.util.Properties;

/**
 * <p> <b> 服务启动预热采样决策 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-03-19
 */
public class HawkEyeStartWarmUpPreSampleDecision implements SampleDecision {

    private long startWarmUpEndMillis;

    @Override
    public int order() {
        return 3;
    }

    @Override
    public void init(Properties config) {
        long startWarmUpMillis      = Long.parseLong(config.getProperty("startWarmUpMillis", "0"));
        this.startWarmUpEndMillis   = System.currentTimeMillis() + startWarmUpMillis;
    }

    @Override
    public boolean isPreDecision() {
        return true;
    }

    @Override
    public void preDecide(TraceContext context, SampleDecisionChain chain) {
        // 服务启动预热阶段则采集
        if (inStartWarmUpStage()) {
            context.setNeedSample(true);
            return;
        }

        chain.preDecide(context);
    }

    private boolean inStartWarmUpStage() {
        return System.currentTimeMillis() < startWarmUpEndMillis;
    }

}
