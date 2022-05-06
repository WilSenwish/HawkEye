package com.littleyes.collector.sample.impl;

import com.littleyes.collector.sample.SampleDecision;

/**
 * <p> <b> 压力测试采样决策 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-03-19
 */
public class HawkEyePressureTestPreSampleDecision implements SampleDecision {

    @Override
    public int order() {
        return 4;
    }

    @Override
    public boolean isPreDecision() {
        return true;
    }

}
