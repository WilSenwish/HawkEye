package com.littleyes.manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * <p> <b> Manager 启动类 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-19
 */
@EnableAutoConfiguration
@SpringBootApplication
public class HawkEyeManagerMainApplication {

    public static void main(String[] args) {
        SpringApplication.run(HawkEyeManagerMainApplication.class, args);
    }

}
