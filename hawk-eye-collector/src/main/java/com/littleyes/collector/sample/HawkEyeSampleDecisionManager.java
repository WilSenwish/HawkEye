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

    private static List<SampleDecision> sampleDecisions = new LinkedList<>();

    static {
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

            Properties sampleConfig = HawkEyeSampleConfig.getInstance();
            sampleDecisions.forEach(e -> e.init(sampleConfig));
        }

        log.info("{} HawkEyeSampleDecisionManager.sampleDecisions initialized.", HAWK_EYE_COLLECTOR);
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
        return new HawkEyeSampleDecisionChain(sampleDecisions).decide(context);
    }

}
