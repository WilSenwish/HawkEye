package com.littleyes.agent.core.health;

import com.littleyes.common.config.HawkEyeConfig;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p> <b> 健康检查控制器 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-19
 */
@RestController
@RequestMapping("health")
public class HealthController {

    @GetMapping("ping")
    public String ping() {
        return "pong";
    }

    @GetMapping("projectName")
    public String projectName() {
        return HawkEyeConfig.getProjectName();
    }

    @GetMapping("gitCommitId")
    public String gitCommitId() {
        return HawkEyeConfig.getGitCommitId();
    }

}
