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
public class SerialMemoryPoolMetricAccessor extends BaseMemoryPoolMetricAccessor {

    public static final String SERIAL_MP_MARKER ="Survivor Space";

    public SerialMemoryPoolMetricAccessor(List<MemoryPoolMXBean> beans) {
        super(beans);
    }

    @Override
    protected String[] getNewGenNames() {
        return new String[] {"Eden Space"};
    }

    @Override
    protected String[] getOldGenNames() {
        return new String[] {"Tenured Gen"};
    }

    @Override
    protected String[] getSurvivorNames() {
        return new String[] {"Survivor Space"};
    }

    @Override
    protected String[] getPermNames() {
        return new String[] {
                "Perm Gen",
                "Compressed Class Space"
        };
    }

}
