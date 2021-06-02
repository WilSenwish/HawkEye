package com.littleyes.collector.sample;

import com.littleyes.common.config.HawkEyeConfig;
import com.littleyes.common.config.HawkEyeConfigLoader;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Properties;

import static com.littleyes.collector.util.Constants.HAWK_EYE_COLLECTOR;

/**
 * <p> <b> 采样配置实现 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-03-19
 */
@Slf4j
public class HawkEyeSampleConfig extends Properties {

    private static final String CONF_RESOURCE_NAME  = "hawk-eye-sample.properties";

    private static Properties config = new HawkEyeSampleConfig();

    static {
        try {
            String configRoot = System.getProperty(HawkEyeConfig.CONF_RESOURCE_DIR);
            if (Objects.isNull(configRoot) || configRoot.isEmpty()) {
                config.putAll(HawkEyeConfigLoader.loadFromClassPath(CONF_RESOURCE_NAME));
            } else {
                config.putAll(HawkEyeConfigLoader.loadFromFileSystem(configRoot, CONF_RESOURCE_NAME));
            }
        } catch (Exception e) {
            log.error("{} Load config[{}] error：{}", HAWK_EYE_COLLECTOR, CONF_RESOURCE_NAME, e.getMessage());
        }
    }

    public static Properties getInstance() {
        return config;
    }

}
