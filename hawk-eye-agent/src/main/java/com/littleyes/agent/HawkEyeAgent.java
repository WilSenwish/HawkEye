package com.littleyes.agent;

import java.lang.instrument.Instrumentation;

/**
 * <p> <b> agent 入口（同时提供 premain 与 agentmain 方法） </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-18
 */
public class HawkEyeAgent {

    public static void premain(String agentArgs, Instrumentation instrumentation) {
        main(agentArgs, instrumentation);
    }

    public static void agentmain(String agentArgs, Instrumentation instrumentation) {
        main(agentArgs, instrumentation);
    }

    private static void main(String agentArgs, Instrumentation instrumentation) {
        // TODO Agent entrance
    }

}
