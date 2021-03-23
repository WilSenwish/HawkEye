package com.littleyes.collector.sample.impl;

import com.littleyes.collector.sample.SampleDecision;
import com.littleyes.collector.sample.SampleDecisionChain;
import com.littleyes.common.trace.TraceContext;

import java.util.Properties;

/**
 * <p> <b> 服务异常采样决策 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-03-19
 */
public class HawkEyeServiceErrorSampleDecision implements SampleDecision {

    @Override
    public int order() {
        return 1;
    }

    @Override
    public void init(Properties config) {

    }

    @Override
    public boolean decide(TraceContext context, SampleDecisionChain chain) {
        // 链路节点发生异常则采集
        if (requestOccursError(context)) {
            return true;
        }

        return chain.decide(context);
    }

    private boolean requestOccursError(TraceContext context) {
        return context.getPerformanceLogs().stream().anyMatch(e -> !e.isSuccess());
    }

}
