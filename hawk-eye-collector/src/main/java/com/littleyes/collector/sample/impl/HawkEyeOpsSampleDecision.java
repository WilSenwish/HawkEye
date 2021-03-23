package com.littleyes.collector.sample.impl;

import com.littleyes.collector.sample.SampleDecision;
import com.littleyes.collector.sample.SampleDecisionChain;
import com.littleyes.common.trace.TraceContext;

import java.util.Properties;

/**
 * <p> <b> OPS采样决策 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-03-19
 */
public class HawkEyeOpsSampleDecision implements SampleDecision {

    @Override
    public int order() {
        return 6;
    }

    @Override
    public void init(Properties config) {

    }

    @Override
    public boolean decide(TraceContext context, SampleDecisionChain chain) {
        return chain.decide(context);
    }

}
