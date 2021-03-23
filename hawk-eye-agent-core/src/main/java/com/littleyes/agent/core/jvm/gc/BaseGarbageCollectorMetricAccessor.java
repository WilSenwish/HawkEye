package com.littleyes.agent.core.jvm.gc;

import com.littleyes.common.dto.jvm.GarbageCollectorMetric;
import com.littleyes.common.dto.jvm.GarbageCollectorPhrase;
import lombok.extern.slf4j.Slf4j;

import java.lang.management.GarbageCollectorMXBean;
import java.util.LinkedList;
import java.util.List;

/**
 * <p> <b> GarbageCollectorMetric 指标数据收集基础实现 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-25
 */
@Slf4j
public abstract class BaseGarbageCollectorMetricAccessor implements GarbageCollectorMetricAccessor {

    private List<GarbageCollectorMXBean> gcMxBeans;

    private long lastMinorCollectionCount = 0;
    private long lastMinorCollectionTime = 0;
    private long lastMajorCollectionCount = 0;
    private long lastMajorCollectionTime = 0;

    public BaseGarbageCollectorMetricAccessor(List<GarbageCollectorMXBean> gcMxBeans) {
        this.gcMxBeans = gcMxBeans;
    }

    @Override
    public List<GarbageCollectorMetric> getGarbageCollectorMetricList() {
        List<GarbageCollectorMetric> garbageCollectorMetricList = new LinkedList<>();

        for (GarbageCollectorMXBean bean : gcMxBeans) {
            String name = bean.getName();
            GarbageCollectorPhrase phrase;
            long gcCount;
            long gcTime;

            if (name.equals(getMinorGarbageCollectorName())) {
                phrase = GarbageCollectorPhrase.MINOR;

                long collectionCount = bean.getCollectionCount();
                gcCount = collectionCount - lastMinorCollectionCount;
                lastMinorCollectionCount = collectionCount;

                long collectionTime = bean.getCollectionTime();
                gcTime = collectionTime - lastMinorCollectionTime;
                lastMinorCollectionTime = collectionTime;
            } else if (name.equals(getMajorGarbageCollectorName())) {
                phrase = GarbageCollectorPhrase.MAJOR;

                long collectionCount = bean.getCollectionCount();
                gcCount = collectionCount - lastMajorCollectionCount;
                lastMajorCollectionCount = collectionCount;

                long collectionTime = bean.getCollectionTime();
                gcTime = collectionTime - lastMajorCollectionTime;
                lastMajorCollectionTime = collectionTime;
            } else {
                continue;
            }

            garbageCollectorMetricList.add(
                    GarbageCollectorMetric.builder()
                            .phrase(phrase)
                            .count(gcCount)
                            .time(gcTime)
                            .build()
            );
        }

        log.debug("ygcc:{}, ygct:{}, ogcc:{}, ogct:{}",
                lastMinorCollectionCount, lastMinorCollectionTime, lastMajorCollectionCount, lastMajorCollectionTime);

        return garbageCollectorMetricList;
    }

    /**
     * MinorGarbageCollectorName
     *
     * @return
     */
    protected abstract String getMinorGarbageCollectorName();

    /**
     * MajorGarbageCollectorName
     *
     * @return
     */
    protected abstract String getMajorGarbageCollectorName();

}
