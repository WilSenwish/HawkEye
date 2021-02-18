package com.littleyes.common.util;

import lombok.extern.slf4j.Slf4j;

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

    private static final Runtime RUNTIME = Runtime.getRuntime();

    public static final int CPU_CORES = RUNTIME.availableProcessors();

    public static void addShutdownHook(Thread hook) {
        RUNTIME.addShutdownHook(hook);
        log.info("{} Add ShutdownHook: {}", HAWK_EYE, hook.getName());
    }

}
