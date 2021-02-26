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
public class G1MemoryPoolMetricAccessor extends BaseMemoryPoolMetricAccessor {

    public static final String G1_MP_MARKER ="G1";

    public G1MemoryPoolMetricAccessor(List<MemoryPoolMXBean> beans) {
        super(beans);
    }

    @Override
    protected String[] getNewGenNames() {
        return new String[] {"G1 Eden Space"};
    }

    @Override
    protected String[] getOldGenNames() {
        return new String[] {"G1 Old Gen"};
    }

    @Override
    protected String[] getSurvivorNames() {
        return new String[] {"G1 Survivor Space"};
    }

    @Override
    protected String[] getPermNames() {
        return new String[] {
                "G1 Perm Gen",
                "Compressed Class Space"
        };
    }

}
