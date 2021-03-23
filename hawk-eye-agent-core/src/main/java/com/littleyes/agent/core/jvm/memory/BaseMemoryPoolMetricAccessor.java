package com.littleyes.agent.core.jvm.memory;

import com.littleyes.common.dto.jvm.MemoryPoolMetric;
import com.littleyes.common.dto.jvm.MemoryPoolType;

import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.LinkedList;
import java.util.List;

/**
 * <p> <b> MemoryPoolMetric 指标数据收集基础实现 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-26
 */
public abstract class BaseMemoryPoolMetricAccessor implements MemoryPoolMetricAccessor {

    private List<MemoryPoolMXBean> beans;

    public BaseMemoryPoolMetricAccessor(List<MemoryPoolMXBean> beans) {
        this.beans = beans;
    }

    @Override
    public List<MemoryPoolMetric> getMemoryPoolMetricList() {
        List<MemoryPoolMetric> poolList = new LinkedList<>();

        for (MemoryPoolMXBean bean : beans) {
            String name = bean.getName();
            MemoryPoolType type;

            if (contains(getCodeCacheNames(), name)) {
                type = MemoryPoolType.CODE_CACHE;
            } else if (contains(getMetaspaceNames(), name)) {
                type = MemoryPoolType.METASPACE;
            } else if (contains(getNewGenNames(), name)) {
                type = MemoryPoolType.NEW_GEN;
            } else if (contains(getOldGenNames(), name)) {
                type = MemoryPoolType.OLD_GEN;
            } else if (contains(getSurvivorNames(), name)) {
                type = MemoryPoolType.SURVIVOR;
            } else if (contains(getPermNames(), name)) {
                type = MemoryPoolType.PERM_GEN;
            } else {
                continue;
            }

            MemoryUsage usage = bean.getUsage();
            poolList.add(
                    MemoryPoolMetric.builder()
                            .type(type)
                            .init(usage.getInit())
                            .used(usage.getUsed())
                            .committed(usage.getCommitted())
                            .max(usage.getMax())
                            .build()
            );
        }

        return poolList;
    }

    private boolean contains(String[] possibleNames, String name) {
        for (String possibleName : possibleNames) {
            if (name.equals(possibleName)) {
                return true;
            }
        }

        return false;
    }

    protected String[] getCodeCacheNames() {
        return new String[] {"Code Cache"};
    }

    protected String[] getMetaspaceNames() {
        return new String[] {"Metaspace"};
    }

    /**
     * NewGenNames
     *
     * @return
     */
    protected abstract String[] getNewGenNames();

    /**
     * OldGenNames
     *
     * @return
     */
    protected abstract String[] getOldGenNames();

    /**
     * SurvivorNames
     *
     * @return
     */
    protected abstract String[] getSurvivorNames();

    /**
     * PermNames
     *
     * @return
     */
    protected abstract String[] getPermNames();

}
