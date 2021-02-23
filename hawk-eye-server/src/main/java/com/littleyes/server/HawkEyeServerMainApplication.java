package com.littleyes.server;

import org.mybatis.spring.annotation.MapperScan;
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
@MapperScan(basePackages = {"com.littleyes.storage.mapper"})
@SpringBootApplication(scanBasePackages = {"com"})
public class HawkEyeServerMainApplication {

    public static void main(String[] args) {
        SpringApplication.run(HawkEyeServerMainApplication.class, args);
    }

}
