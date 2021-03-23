package com.littleyes.collector.sample;

import com.littleyes.common.trace.TraceContext;

/**
 * <p> <b> 采样决策链 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-03-19
 */
public interface SampleDecisionChain {

    /**
     * 决策
     *
     * @param context
     * @return
     */
    boolean decide(TraceContext context);

}
