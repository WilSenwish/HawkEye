package com.littleyes.collector.sample;

import com.littleyes.common.trace.TraceContext;

import java.util.List;

/**
 * <p> <b> 采样决策链实现 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-03-19
 */
public class HawkEyeSampleDecisionChain implements SampleDecisionChain {

    private int currentPosition = 0;

    private final List<? extends SampleDecision> sampleDecisions;

    HawkEyeSampleDecisionChain(List<? extends SampleDecision> sampleDecisions) {
        this.sampleDecisions = sampleDecisions;
    }

    @Override
    public boolean decide(TraceContext context) {
        if (this.currentPosition == this.sampleDecisions.size()) {
            return false;
        } else {
            this.currentPosition++;
            SampleDecision nextSampleDecision = this.sampleDecisions.get(this.currentPosition - 1);
            return nextSampleDecision.decide(context, this);
        }
    }

}
