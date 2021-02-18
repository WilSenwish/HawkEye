package com.littleyes.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static com.littleyes.common.config.HawkEyeConfig.HAWK_EYE;

/**
 * <p> <b> 配置文件加载器 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-18
 */
@Slf4j
public class HawkEyeConfigLoader {

    private HawkEyeConfigLoader() {
    }

    public static Properties loadFromClassPath(String resource) throws IOException {
        log.info("{} Load class path resource [{}]", HAWK_EYE, resource);
        return PropertiesLoaderUtils.loadAllProperties(resource);
    }

    public static Properties loadFromFileSystem(String configRoot, String resource) throws IOException {
        resource = configRoot + File.separator + resource;
        Properties properties = new Properties();

        log.info("{} Load file system resource [{}]", HAWK_EYE, resource);
        try (InputStream is = new FileInputStream(resource)) {
            properties.load(is);
        }

        return properties;
    }

}
