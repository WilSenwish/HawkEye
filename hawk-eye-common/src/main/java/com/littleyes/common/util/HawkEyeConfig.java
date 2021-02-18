package com.littleyes.common.util;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Properties;

/**
 * <p> <b> 配置 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-18
 */
@Slf4j
public class HawkEyeConfig {

    public static final String HAWK_EYE             = "HawkEye ===> |";
    private static final String CONF_RESOURCE_DIR   = "hawk-eye-config.root";
    private static final String CONF_RESOURCE_NAME  = "hawk-eye-config.properties";
    private static final String GIT_RESOURCE_NAME   = "git.properties";

    private HawkEyeConfig() {
    }

    private static String projectName           = "HAWK_EYE_default";
    private static long collectorSpinWaitMills  = 30L;
    private static boolean performanceEnabled   = false;
    private static boolean loggingEnabled       = false;
    private static String loggingCollectLevel   = "INFO";

    private static String commitId              = "please packaged with[pl.project13.maven:git-commit-id-plugin]";

    static {
        try {
            Properties properties;

            String configRoot = System.getProperty(CONF_RESOURCE_DIR);
            if (Objects.isNull(configRoot) || configRoot.isEmpty()) {
                properties = HawkEyeConfigLoader.loadFromClassPath(CONF_RESOURCE_NAME);
            } else {
                properties = HawkEyeConfigLoader.loadFromFileSystem(configRoot, CONF_RESOURCE_NAME);
            }

            projectName             = properties.getProperty("hawk-eye.project-name", projectName);
            collectorSpinWaitMills  = Long.parseLong(properties.getProperty("hawk-eye.collector-spin-wait-mills", "30"));
            performanceEnabled      = Boolean.parseBoolean(properties.getProperty("hawk-eye.performance-enabled",  "false"));
            loggingEnabled          = Boolean.parseBoolean(properties.getProperty("hawk-eye.logging-enabled",  "false"));
            loggingCollectLevel     = properties.getProperty("hawk-eye.logging-collect-level", loggingCollectLevel);
        } catch (Exception e) {
            log.error("{} Load config[{}] error：{}", HAWK_EYE, CONF_RESOURCE_NAME, e.getMessage());
        }

        try {
            Properties properties   = HawkEyeConfigLoader.loadFromClassPath(GIT_RESOURCE_NAME);
            commitId                = properties.getProperty("git.commit.id", commitId);
        } catch (Exception e) {
            log.error("{} Load config[{}] error：{}", HAWK_EYE, GIT_RESOURCE_NAME, e.getMessage());
        }
    }

    public static String getProjectName() {
        return projectName;
    }

    public static long getCollectorSpinWaitMills() {
        return collectorSpinWaitMills;
    }

    public static boolean isPerformanceEnabled() {
        return performanceEnabled;
    }

    public static boolean isPerformanceDisabled() {
        return !isPerformanceEnabled();
    }

    public static boolean isLoggingEnabled() {
        return loggingEnabled;
    }

    public static boolean isLoggingDisabled() {
        return !isLoggingEnabled();
    }

    public static String getLoggingCollectLevel() {
        return loggingCollectLevel;
    }

    public static String getCommitId() {
        return commitId;
    }

}
