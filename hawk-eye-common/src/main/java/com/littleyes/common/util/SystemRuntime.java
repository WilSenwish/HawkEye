package com.littleyes.common.util;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

import static com.littleyes.common.config.HawkEyeConfig.HAWK_EYE;

/**
 * <p> <b> 系统本身运行环境 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-18
 */
@Slf4j
public class SystemRuntime {

    public SystemRuntime() {
    }

    private static Runtime runtime = Runtime.getRuntime();

    private static AtomicInteger shutdownHookNum = new AtomicInteger();

    public static final int CPU_CORES = runtime.availableProcessors();

    public static void addShutdownHook(Thread hook) {
        runtime.addShutdownHook(hook);
        log.info("{} Added [NO.{}] ShutdownHook[{}]!", HAWK_EYE, shutdownHookNum.incrementAndGet(), hook.getName());
    }

}
