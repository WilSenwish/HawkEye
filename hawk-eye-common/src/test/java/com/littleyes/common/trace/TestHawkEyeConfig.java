package com.littleyes.common.trace;

import com.littleyes.common.config.HawkEyeConfig;

public class TestHawkEyeConfig {
    public static void main(String[] args) {
        System.setProperty("hawk-eye-config.root", "D:\\opt");
        System.out.println(HawkEyeConfig.getProjectName());
        System.out.println(HawkEyeConfig.getCommitId());
    }
}
