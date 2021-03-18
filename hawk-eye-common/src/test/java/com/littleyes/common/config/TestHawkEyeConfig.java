package com.littleyes.common.config;

public class TestHawkEyeConfig {
    public static void main(String[] args) {
        System.setProperty("hawk-eye-config.root", "D:\\opt");
        System.out.println(HawkEyeConfig.getProjectName());
        System.out.println(HawkEyeConfig.getGitCommitId());
    }
}
