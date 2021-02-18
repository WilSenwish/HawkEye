package com.littleyes.agent;

import java.lang.instrument.Instrumentation;

/**
 * @Description agent 入口（同时提供 premain 与 agentmain 方法）
 *
 * @author Junbing.Chen
 * @date 2021-02-18
 */
public class LatteAgent {

    public static void premain(String agentArgs, Instrumentation instrumentation) {
        agentmain(agentArgs, instrumentation);
    }

    public static void agentmain(String agentArgs, Instrumentation instrumentation) {
        // TODO Agent entrance
    }

}
