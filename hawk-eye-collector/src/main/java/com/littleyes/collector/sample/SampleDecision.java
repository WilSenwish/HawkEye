package com.littleyes.collector.sample;

import com.littleyes.common.dto.PerformanceLogDto;
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
    default int order() {
        return 0;
    }

    /**
     * 初始化
     *
     * @param config
     */
    default void init(Properties config) {

    }

    /**
     * 决策类型
     *
     * @return
     */
    default boolean isPreDecision() {
        return false;
    }

    /**
     * 决策
     *
     * @param context
     * @param chain
     * @return
     */
    default void preDecide(TraceContext context, SampleDecisionChain chain) {
        chain.preDecide(context);
    }

    /**
     * 决策
     *
     * @param context
     * @param performanceLog
     * @param chain
     * @return
     */
    default boolean postDecide(TraceContext context, PerformanceLogDto performanceLog, SampleDecisionChain chain) {
        return chain.postDecide(context, performanceLog);
    }

}
