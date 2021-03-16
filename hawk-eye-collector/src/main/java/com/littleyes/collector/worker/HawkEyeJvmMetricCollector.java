package com.littleyes.collector.worker;

import com.littleyes.collector.dto.jvm.JvmMetric;
import com.littleyes.collector.spi.JvmMetricDelivery;
import com.littleyes.common.core.PluginLoader;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

import static com.littleyes.collector.util.Constants.HAWK_EYE_COLLECTOR;
import static org.apache.commons.lang3.time.DateUtils.MILLIS_PER_MINUTE;

/**
 * <p> <b> JvmMetric收集任务线程 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-03-16
 */
@Slf4j
public class HawkEyeJvmMetricCollector extends BaseCollector<JvmMetric> {

    public HawkEyeJvmMetricCollector(BlockingQueue<JvmMetric> bufferQueue) {
        super(HawkEyeJvmMetricCollector.class.getSimpleName(), bufferQueue);
    }

    @Override
    protected long getCollectorSpinWaitMills() {
        return MILLIS_PER_MINUTE;
    }

    @Override
    protected void send(List<JvmMetric> metrics) {
        try {
            PluginLoader.of(JvmMetricDelivery.class).load()
                    .deliver(metrics.parallelStream().peek(JvmMetric::initBase).collect(Collectors.toList()));
        } catch (Exception e) {
            log.error("{} send jvm metrics error", HAWK_EYE_COLLECTOR, e);
        }
    }

}
