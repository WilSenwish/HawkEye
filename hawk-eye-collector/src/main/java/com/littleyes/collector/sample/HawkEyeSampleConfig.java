package com.littleyes.collector.sample;

import java.util.Properties;

/**
 * <p> <b> 采样配置实现 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-03-19
 */
public class HawkEyeSampleConfig extends Properties {

    private static HawkEyeSampleConfig config = new HawkEyeSampleConfig();

    public static HawkEyeSampleConfig getInstance() {
        return config;
    }

}
