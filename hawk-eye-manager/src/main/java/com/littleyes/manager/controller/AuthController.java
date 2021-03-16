package com.littleyes.manager.controller;

import com.littleyes.common.core.PluginLoader;
import com.littleyes.storage.spi.ValidateCodeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * <p> <b> 认证 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-23
 */
@Slf4j
@RestController
@RequestMapping("auth")
public class AuthController {

    @GetMapping("validateCode")
    public Map<String, Object> generateValidateCode() {
        return PluginLoader.of(ValidateCodeRepository.class).load().generate();
    }

}
