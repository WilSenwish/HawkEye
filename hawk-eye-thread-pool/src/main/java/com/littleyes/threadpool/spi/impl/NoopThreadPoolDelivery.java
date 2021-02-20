package com.littleyes.threadpool.spi.impl;

import com.littleyes.common.ep.SPI;
import com.littleyes.threadpool.spi.ThreadPoolDelivery;
import com.littleyes.threadpool.util.ThreadPoolExecutorInfo;
import com.littleyes.threadpool.util.ThreadPoolExecutorStat;
import lombok.extern.slf4j.Slf4j;

/**
 * <p> <b> 默认线程池监控数据接口实现 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-19
 */
@Slf4j
@SPI
public class NoopThreadPoolDelivery implements ThreadPoolDelivery {

    @Override
    public void deliverThreadPoolExecutorInfo(ThreadPoolExecutorInfo info) {
        if (log.isDebugEnabled()) {
            log.debug("{}", info);
        }
    }

    @Override
    public void deliverThreadPoolExecutorStat(ThreadPoolExecutorStat stat) {
        if (log.isDebugEnabled()) {
            log.debug("{}", stat);
        }
    }

}
