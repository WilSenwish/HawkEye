package com.littleyes.collector.sample.impl;

import com.littleyes.collector.sample.SampleDecision;
import com.littleyes.collector.sample.SampleDecisionChain;
import com.littleyes.common.trace.TraceContext;

import java.util.Properties;

/**
 * <p> <b> 服务调试采样决策 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-03-19
 */
public class HawkEyeServiceDebugSampleDecision implements SampleDecision {

    @Override
    public int order() {
        return 0;
    }

    @Override
    public void init(Properties config) {

    }

    @Override
    public boolean decide(TraceContext context, SampleDecisionChain chain) {
        // 开启 Debug 调试则采集
        if (context.isDebugMode()) {
            return true;
        }

        return chain.decide(context);
    }

}
