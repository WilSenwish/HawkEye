package com.littleyes.collector.sample.impl;

import com.littleyes.collector.sample.SampleDecision;
import com.littleyes.collector.sample.SampleDecisionChain;
import com.littleyes.common.trace.TraceContext;

/**
 * <p> <b> 服务调试采样决策 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-03-19
 */
public class HawkEyeServiceDebugPreSampleDecision implements SampleDecision {

    @Override
    public int order() {
        return 0;
    }

    @Override
    public boolean isPreDecision() {
        return true;
    }

    @Override
    public void preDecide(TraceContext context, SampleDecisionChain chain) {
        // 开启 Debug 调试则采集
        if (context.isDebugMode()) {
            context.setNeedSample(true);
            return;
        }

        chain.preDecide(context);
    }

}
