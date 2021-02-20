package com.littleyes.threadpool.spi;

import com.littleyes.common.core.SPI;
import com.littleyes.threadpool.util.ThreadPoolExecutorInfo;
import com.littleyes.threadpool.util.ThreadPoolExecutorStat;

/**
 * <p> <b> 线程池监控数据接口 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-19
 */
@SPI
public interface ThreadPoolDelivery {

    /**
     * 分发或存储线程池基础信息
     *
     * @param info
     */
    default void deliverThreadPoolExecutorInfo(ThreadPoolExecutorInfo info) {
    }

    /**
     * 分发或存储线程池状态信息
     *
     * @param stat
     */
    default void deliverThreadPoolExecutorStat(ThreadPoolExecutorStat stat) {
    }

}
