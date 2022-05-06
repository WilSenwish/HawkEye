package com.littleyes.common.config;

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

    public static final String HAWK_EYE_COMMON      = "@HawkEyeCommon ===> |";
    public static final String CONF_RESOURCE_DIR    = "hawk-eye-config.root";

    private static final String CONF_RESOURCE_NAME  = "hawk-eye-config.properties";
    private static final String GIT_RESOURCE_NAME   = "git.properties";
    private static final PortHolder PORT_HOLDER     = new PortHolder();

    private HawkEyeConfig() {
    }

    private static String   projectName             = "HAWK_EYE_default";
    private static String   gitCommitId             = "please packaged with[pl.project13.maven:git-commit-id-plugin]";

    private static long     collectorSpinWaitMills  = 30L;
    private static boolean  performanceEnabled      = false;
    private static boolean  loggingEnabled          = false;
    private static boolean  alarmEnabled            = false;
    private static String   loggingCollectLevel     = "INFO";

    static {
        initMainProperties();

        initGitProperties();
    }

    private static void initMainProperties() {
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
            alarmEnabled            = Boolean.parseBoolean(properties.getProperty("hawk-eye.alarm-enabled",  "true"));
            loggingCollectLevel     = properties.getProperty("hawk-eye.logging-collect-level", loggingCollectLevel);
        } catch (Exception e) {
            log.error("{} Load config[{}] error：{}", HAWK_EYE_COMMON, CONF_RESOURCE_NAME, e.getMessage());
        }
    }

    private static void initGitProperties() {
        try {
            Properties properties   = HawkEyeConfigLoader.loadFromClassPath(GIT_RESOURCE_NAME);
            gitCommitId             = properties.getProperty("git.commit.id", gitCommitId);
        } catch (Exception e) {
            log.error("{} Load config[{}] error：{}", HAWK_EYE_COMMON, GIT_RESOURCE_NAME, e.getMessage());
        }
    }

    public static String getProjectName() {
        return projectName;
    }

    public static String getGitCommitId() {
        return gitCommitId;
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

    public static boolean isAlarmEnabled() {
        return alarmEnabled;
    }

    public static boolean isAlarmDisabled() {
        return !isAlarmEnabled();
    }

    public static String getLoggingCollectLevel() {
        return loggingCollectLevel;
    }

    public synchronized static void recordServerPort(Integer serverPort) {
        if (Objects.nonNull(PORT_HOLDER.port) && !Objects.equals(PORT_HOLDER.port, serverPort)) {
            log.warn("{} Override [PORT_HOLDER.port] from [{}] to [{}]", HAWK_EYE_COMMON, PORT_HOLDER.port, serverPort);
        }

        PORT_HOLDER.port = serverPort;
    }

    public static Integer getServerPort() {
        return PORT_HOLDER.port;
    }

    private static class PortHolder {
        private Integer port;
    }

}
