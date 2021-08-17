package com.littleyes.collector.worker;

import com.littleyes.collector.spi.AlarmMessageDelivery;
import com.littleyes.common.core.PluginLoader;
import com.littleyes.common.dto.AlarmMessageDto;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

import static com.littleyes.collector.util.Constants.HAWK_EYE_COLLECTOR;
import static org.apache.commons.lang3.time.DateUtils.MILLIS_PER_MINUTE;

/**
 * <p> <b> AlarmMessage收集任务线程 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-03-16
 */
@Slf4j
public class HawkEyeAlarmMessageCollector extends BaseCollector<AlarmMessageDto> {

    public HawkEyeAlarmMessageCollector(BlockingQueue<AlarmMessageDto> bufferQueue) {
        super(HawkEyeAlarmMessageCollector.class.getSimpleName(), bufferQueue);
    }

    @Override
    protected long getCollectorSpinWaitMills() {
        return MILLIS_PER_MINUTE;
    }

    @Override
    protected void send(List<AlarmMessageDto> messages) {
        try {
            PluginLoader.of(AlarmMessageDelivery.class).load()
                    .deliver(messages.parallelStream().peek(AlarmMessageDto::initBase).collect(Collectors.toList()));
        } catch (Exception e) {
            log.error("{} send alarm message error", HAWK_EYE_COLLECTOR, e);
        }
    }

}
