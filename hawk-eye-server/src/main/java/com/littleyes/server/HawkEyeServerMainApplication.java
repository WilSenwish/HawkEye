package com.littleyes.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * <p> <b> Server 启动类 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-19
 */
@EnableAutoConfiguration
@SpringBootApplication(scanBasePackages = {"com"})
public class HawkEyeServerMainApplication {

    public static void main(String[] args) {
        SpringApplication.run(HawkEyeServerMainApplication.class, args);
    }

}
