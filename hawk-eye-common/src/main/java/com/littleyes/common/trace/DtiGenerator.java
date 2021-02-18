package com.littleyes.common.trace;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <p> <b> 分布式链路追踪 ID 生成器 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-18
 */
public class DtiGenerator {

    /**
     * not out of bound of short!!!
     */
    private static final long SEQ_BOUND = 10000L;

    private static final String APP_INSTANCE_ID = UUID.randomUUID().toString().replaceAll("-", "");

    private static final ThreadLocal<DtiContext> THREAD_DTI_SEQUENCE = ThreadLocal.withInitial(DtiContext::new);

    private DtiGenerator() {
    }

    /**
     * Generate a new id, combined by three parts.
     * <p>
     * The first one represents application instance id.
     * <p>
     * The second one represents thread id.
     * <p>
     * The third one also has two parts, 1) a timestamp, measured in milliseconds 2) a seq, in current thread, between
     * 0(included) and 9999(included)
     *
     * @return unique id to represent a trace or segment
     */
    public static String generate() {
        Object[] elements = {
                APP_INSTANCE_ID,
                Thread.currentThread().getId(),
                THREAD_DTI_SEQUENCE.get().nextSeq()
        };

        return Arrays.stream(elements).map(String::valueOf).collect(Collectors.joining("."));
    }

    private static class DtiContext {
        private long lastTimestamp;
        private short threadSeq;

        // Just for considering time-shift-back only.
        private long runRandomTimestamp;
        private int lastRandomValue;
        private SecureRandom random;

        private DtiContext() {
            this.lastTimestamp = System.currentTimeMillis();
            this.threadSeq = 0;
        }

        private long nextSeq() {
            return timestamp() * SEQ_BOUND + nextThreadSeq();
        }

        private long timestamp() {
            long currentTimeMillis = System.currentTimeMillis();

            if (currentTimeMillis < lastTimestamp) {
                // Just for considering time-shift-back by Ops or OS. @hanahmily 's suggestion.
                if (random == null) {
                    random = new SecureRandom();
                }

                if (runRandomTimestamp != currentTimeMillis) {
                    lastRandomValue = random.nextInt();
                    runRandomTimestamp = currentTimeMillis;
                }

                return lastRandomValue;
            } else {
                lastTimestamp = currentTimeMillis;
                return lastTimestamp;
            }
        }

        private short nextThreadSeq() {
            if (threadSeq == SEQ_BOUND) {
                threadSeq = 0;
            }

            return threadSeq++;
        }
    }

}
