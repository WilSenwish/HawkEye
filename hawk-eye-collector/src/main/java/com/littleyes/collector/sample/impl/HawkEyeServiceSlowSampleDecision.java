package com.littleyes.collector.sample.impl;

import com.littleyes.collector.sample.SampleDecision;
import com.littleyes.collector.sample.SampleDecisionChain;
import com.littleyes.common.trace.TraceContext;

import java.util.Properties;

/**
 * <p> <b> 服务缓慢采样决策 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-03-19
 */
public class HawkEyeServiceSlowSampleDecision implements SampleDecision {

    private boolean serviceSlowEnabled;
    private long serviceSlowMillis;

    @Override
    public int order() {
        return 2;
    }

    @Override
    public void init(Properties config) {
        this.serviceSlowMillis  = Long.parseLong(config.getProperty("serviceSlowMillis", "0"));
        this.serviceSlowEnabled = this.serviceSlowMillis > 0;
    }

    @Override
    public boolean decide(TraceContext context, SampleDecisionChain chain) {
        // 服务响应慢则采集
        if (this.serviceSlowEnabled && requestServiceSlow(context)) {
            return true;
        }

        return chain.decide(context);
    }

    private boolean requestServiceSlow(TraceContext context) {
        return context.getPerformanceLogs().stream().anyMatch(e -> e.getTimeConsume() > serviceSlowMillis);
    }

}
