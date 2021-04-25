package com.littleyes.collector.sample;

import com.littleyes.common.config.HawkEyeConfig;
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
        initSampleDecisions();
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

        return HawkEyeSampleDecisionChain.startChainDecide(context, sampleDecisions);
    }

    private static void initSampleDecisions() {
        if (HawkEyeConfig.isPerformanceDisabled()) {
            log.info("{} HawkEyeSampleDecisionManager disabled for performance disabled.", HAWK_EYE_COLLECTOR);
            return;
        }

        Properties sampleConfig = HawkEyeSampleConfig.getInstance();

        fullSample = isFullSampleRate(sampleConfig);
        if (fullSample) {
            log.info("{} HawkEyeSampleDecisionManager with fullSample not need sampleDecisions.", HAWK_EYE_COLLECTOR);
            return;
        }

        loadSampleDecisions();
        if (HawkEyeCollectionUtils.isEmpty(sampleDecisions)) {
            return;
        }

        initializeSampleDecisions(sampleConfig);
        log.info("{} HawkEyeSampleDecisionManager initialized [{}] SampleDecisions.",
                HAWK_EYE_COLLECTOR, sampleDecisions.size());
    }

    private static boolean isFullSampleRate(Properties config) {
        int globalSampleRate = Integer.parseInt(config.getProperty(GLOBAL_SAMPLE_RATE_KEY, "20"));
        return globalSampleRate >= DEFAULT_SAMPLE_RATE_BASE;
    }

    private static void loadSampleDecisions() {
        try {
            ServiceLoader.load(SampleDecision.class).forEach(sampleDecisions::add);
            sampleDecisions.sort(Comparator.comparing(SampleDecision::order));
        } catch (Exception e) {
            log.error("{} HawkEyeSampleDecisionManager load SampleDecisions error: {}",
                    HAWK_EYE_COLLECTOR, e.getMessage(), e);
        }
    }

    private static void initializeSampleDecisions(Properties sampleConfig) {
        for (SampleDecision sampleDecision : sampleDecisions) {
            sampleDecision.init(sampleConfig);
            log.info("{} HawkEyeSampleDecisionManager load and initialized SampleDecision [{}]",
                    HAWK_EYE_COLLECTOR, sampleDecision);
        }
    }

}
