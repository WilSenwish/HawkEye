package com.littleyes.collector.sample;

import com.littleyes.common.trace.TraceContext;
import com.littleyes.common.util.HawkEyeCollectionUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import static com.littleyes.collector.util.Constants.HAWK_EYE_COLLECTOR;

/**
 * <p> <b> 采样决策管理器 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-03-19
 */
@Slf4j
public class HawkEyeSampleDecisionManager {

    public static final int DEFAULT_SAMPLE_RATE_BASE = 100;
    public static final String GLOBAL_SAMPLE_RATE_KEY = "globalSampleRate";

    private static List<SampleDecision> sampleDecisions = new LinkedList<>();
    private static boolean fullSample;

    static {
        Properties sampleConfig = HawkEyeSampleConfig.getInstance();
        fullSample = isFullSampleRate(sampleConfig);

        if (fullSample) {
            log.info("{} HawkEyeSampleDecisionManager with fullSample not need sampleDecisions.", HAWK_EYE_COLLECTOR);
        } else {
            try {
                ServiceLoader.load(SampleDecision.class).forEach(sampleDecisions::add);
            } catch (Exception e) {
                log.error("{} Load SampleDecisions error: {}", HAWK_EYE_COLLECTOR, e.getMessage(), e);
            }

            if (HawkEyeCollectionUtils.isNotEmpty(sampleDecisions)) {
                sampleDecisions.sort(Comparator.comparing(SampleDecision::order));
                for (SampleDecision sampleDecision : sampleDecisions) {
                    log.info("{} Loaded SampleDecision [{}]", HAWK_EYE_COLLECTOR, sampleDecision);
                }
                log.info("{} Loaded [{}] SampleDecisions", HAWK_EYE_COLLECTOR, sampleDecisions.size());

                sampleDecisions.forEach(e -> e.init(sampleConfig));
            }

            log.info("{} HawkEyeSampleDecisionManager.sampleDecisions initialized.", HAWK_EYE_COLLECTOR);
        }
    }

    public static void init() {
    }

    /**
     * 决策链决策入口
     *
     * @param context
     * @return
     */
    public static boolean decide(TraceContext context) {
        if (fullSample) {
            return true;
        }

        return new HawkEyeSampleDecisionChain(sampleDecisions).decide(context);
    }

    private static boolean isFullSampleRate(Properties config) {
        int globalSampleRate = Integer.parseInt(config.getProperty(GLOBAL_SAMPLE_RATE_KEY, "20"));
        return globalSampleRate >= DEFAULT_SAMPLE_RATE_BASE;
    }

}
