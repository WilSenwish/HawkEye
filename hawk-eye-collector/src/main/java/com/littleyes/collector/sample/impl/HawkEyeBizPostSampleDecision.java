package com.littleyes.collector.sample.impl;

import com.littleyes.collector.sample.SampleDecision;

/**
 * <p> <b> 业务采样决策 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-03-19
 */
public class HawkEyeBizPostSampleDecision implements SampleDecision {

    @Override
    public int order() {
        return 5;
    }

}
