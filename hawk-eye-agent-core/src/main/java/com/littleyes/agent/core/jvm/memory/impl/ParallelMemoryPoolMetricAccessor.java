package com.littleyes.agent.core.jvm.memory.impl;

import com.littleyes.agent.core.jvm.memory.BaseMemoryPoolMetricAccessor;

import java.lang.management.MemoryPoolMXBean;
import java.util.List;

/**
 * <p> <b> MemoryPoolMetricAccessor 指标数据收集实现 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-26
 */
public class ParallelMemoryPoolMetricAccessor extends BaseMemoryPoolMetricAccessor {

    public static final String PARALLEL_MP_MARKER ="PS";

    public ParallelMemoryPoolMetricAccessor(List<MemoryPoolMXBean> beans) {
        super(beans);
    }

    @Override
    protected String[] getNewGenNames() {
        return new String[] {"PS Eden Space"};
    }

    @Override
    protected String[] getOldGenNames() {
        return new String[] {"PS Old Gen"};
    }

    @Override
    protected String[] getSurvivorNames() {
        return new String[] {"PS Survivor Space"};
    }

    @Override
    protected String[] getPermNames() {
        return new String[] {
                "PS Perm Gen",
                "Compressed Class Space"
        };
    }

}
