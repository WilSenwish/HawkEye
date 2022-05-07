package com.littleyes.collector.sample;

import com.littleyes.common.config.HawkEyeConfig;
import com.littleyes.common.dto.PerformanceLogDto;
import com.littleyes.common.trace.TraceContext;
import com.littleyes.common.util.HawkEyeCollectionUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.ServiceLoader;

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

    private static List<SampleDecision> preSampleDecisions = new LinkedList<>();
    private static List<SampleDecision> postSampleDecisions = new LinkedList<>();
    private static boolean fullSample;

    static {
        initSampleDecisions();
    }

    /**
     * 决策链决策入口
     *
     * @param context
     * @return
     */
    public static void preDecide(TraceContext context) {
        if (context.isNeedSample()) {
            return;
        }

        if (fullSample) {
            context.setNeedSample(true);
            return;
        }

        HawkEyeSampleDecisionChain
                .startChainPreDecide(context, Collections.unmodifiableList(postSampleDecisions));
    }

    /**
     * 决策链决策入口
     *
     * @param context
     * @param performanceLog
     * @return
     */
    public static boolean postDecide(TraceContext context, PerformanceLogDto performanceLog) {
        if (fullSample || context.isNeedSample()) {
            return true;
        }

        return HawkEyeSampleDecisionChain
                .startChainPostDecide(context, performanceLog, Collections.unmodifiableList(postSampleDecisions));
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
        if (HawkEyeCollectionUtils.isEmpty(preSampleDecisions) && HawkEyeCollectionUtils.isEmpty(postSampleDecisions)) {
            return;
        }

        initializeSampleDecisions(sampleConfig);
        log.info("{} HawkEyeSampleDecisionManager initialized [{}] SampleDecisions.",
                HAWK_EYE_COLLECTOR, (preSampleDecisions.size() + postSampleDecisions.size()));
    }

    private static boolean isFullSampleRate(Properties config) {
        int globalSampleRate = Integer.parseInt(config.getProperty(GLOBAL_SAMPLE_RATE_KEY, "20"));
        return globalSampleRate >= DEFAULT_SAMPLE_RATE_BASE;
    }

    private static void loadSampleDecisions() {
        try {
            ServiceLoader.load(SampleDecision.class).forEach(sd -> {
                if (sd.isPreDecision()) {
                    preSampleDecisions.add(sd);
                } else {
                    postSampleDecisions.add(sd);
                }
            });

            preSampleDecisions.sort(Comparator.comparing(SampleDecision::order));
            postSampleDecisions.sort(Comparator.comparing(SampleDecision::order));
        } catch (Exception e) {
            log.error("{} HawkEyeSampleDecisionManager load SampleDecisions error: {}",
                    HAWK_EYE_COLLECTOR, e.getMessage(), e);
        }
    }

    private static void initializeSampleDecisions(Properties sampleConfig) {
        for (SampleDecision sampleDecision : preSampleDecisions) {
            sampleDecision.init(sampleConfig);
            log.info("{} HawkEyeSampleDecisionManager load and initialized SampleDecision [{}]",
                    HAWK_EYE_COLLECTOR, sampleDecision);
        }
        for (SampleDecision sampleDecision : postSampleDecisions) {
            sampleDecision.init(sampleConfig);
            log.info("{} HawkEyeSampleDecisionManager load and initialized SampleDecision [{}]",
                    HAWK_EYE_COLLECTOR, sampleDecision);
        }
    }

}
