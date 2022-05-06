package com.littleyes.collector.sample.impl;

import com.littleyes.collector.sample.SampleDecision;
import com.littleyes.collector.sample.SampleDecisionChain;
import com.littleyes.common.dto.PerformanceLogDto;
import com.littleyes.common.trace.TraceContext;

/**
 * <p> <b> 服务异常采样决策 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-03-19
 */
public class HawkEyeServiceErrorPostSampleDecision implements SampleDecision {

    @Override
    public int order() {
        return 1;
    }

    @Override
    public boolean postDecide(TraceContext context, PerformanceLogDto performanceLog, SampleDecisionChain chain) {
        // 链路节点发生异常则采集
        if (requestOccursError(performanceLog)) {
            return true;
        }

        return chain.postDecide(context, performanceLog);
    }

    private boolean requestOccursError(PerformanceLogDto performanceLogDto) {
        return !performanceLogDto.isSuccess();
    }

}
