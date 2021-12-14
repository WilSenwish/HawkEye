package com.littleyes.collector.logging;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import static com.littleyes.collector.util.Constants.HAWK_EYE_COLLECTOR;

/**
 * <p> <b> MDC </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-12-14
 */
@Slf4j
public class HawkEyeMdc {

    private static final String TRACE_ID_KEY = "TID";

    private HawkEyeMdc() {
    }

    static {
        log.info("{} Current MDCAdapter is [{}]", HAWK_EYE_COLLECTOR, MDC.getMDCAdapter());
    }

    public static void put(String val) {
        MDC.put(TRACE_ID_KEY, val);
    }

}
