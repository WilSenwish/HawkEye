package com.littleyes.collector.sample;

import com.littleyes.common.trace.TraceContext;

import java.util.Properties;

/**
 * <p> <b> 采样决策 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-03-19
 */
public interface SampleDecision {

    /**
     * 顺序（按 从小到大 排 先后顺序）
     *
     * @return
     */
    int order();

    /**
     * 初始化
     *
     * @param config
     */
    void init(Properties config);

    /**
     * 决策
     *
     * @param context
     * @param chain
     * @return
     */
    boolean decide(TraceContext context, SampleDecisionChain chain);

}
