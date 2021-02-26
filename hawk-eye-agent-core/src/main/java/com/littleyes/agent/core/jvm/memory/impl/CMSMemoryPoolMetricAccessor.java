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
public class CMSMemoryPoolMetricAccessor extends BaseMemoryPoolMetricAccessor {

    public static final String CMS_MP_MARKER ="CMS";

    public CMSMemoryPoolMetricAccessor(List<MemoryPoolMXBean> beans) {
        super(beans);
    }

    @Override
    protected String[] getNewGenNames() {
        return new String[] {"Par Eden Space"};
    }

    @Override
    protected String[] getOldGenNames() {
        return new String[] {"CMS Old Gen"};
    }

    @Override
    protected String[] getSurvivorNames() {
        return new String[] {"Par Survivor Space"};
    }

    @Override
    protected String[] getPermNames() {
        return new String[] {
                "CMS Perm Gen",
                "Compressed Class Space"
        };
    }

}
