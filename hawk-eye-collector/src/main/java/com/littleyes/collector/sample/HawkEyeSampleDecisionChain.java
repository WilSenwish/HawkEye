package com.littleyes.collector.sample;

import com.littleyes.common.dto.PerformanceLogDto;
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

    private HawkEyeSampleDecisionChain(List<? extends SampleDecision> sampleDecisions) {
        this.sampleDecisions = sampleDecisions;
    }

    static void startChainPreDecide(TraceContext context, List<SampleDecision> sampleDecisions) {
        new HawkEyeSampleDecisionChain(sampleDecisions).preDecide(context);
    }

    static boolean startChainPostDecide(TraceContext context, PerformanceLogDto performanceLog, List<SampleDecision> sampleDecisions) {
        return new HawkEyeSampleDecisionChain(sampleDecisions).postDecide(context, performanceLog);
    }

    @Override
    public void preDecide(TraceContext context) {
        if (this.currentPosition < this.sampleDecisions.size()) {
            this.currentPosition++;
            SampleDecision nextSampleDecision = this.sampleDecisions.get(this.currentPosition - 1);
            nextSampleDecision.preDecide(context, this);
        }
    }

    @Override
    public boolean postDecide(TraceContext context, PerformanceLogDto performanceLog) {
        if (this.currentPosition == this.sampleDecisions.size()) {
            return false;
        } else {
            this.currentPosition++;
            SampleDecision nextSampleDecision = this.sampleDecisions.get(this.currentPosition - 1);
            return nextSampleDecision.postDecide(context, performanceLog, this);
        }
    }

}
