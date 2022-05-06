package com.littleyes.collector.sample.impl;

import com.littleyes.collector.sample.SampleDecision;

/**
 * <p> <b> OPS采样决策 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-03-19
 */
public class HawkEyeOpsPreSampleDecision implements SampleDecision {

    @Override
    public int order() {
        return 6;
    }

    @Override
    public boolean isPreDecision() {
        return true;
    }

}
