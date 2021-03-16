package com.littleyes.manager.service.impl;

import com.littleyes.common.core.PluginLoader;
import com.littleyes.manager.query.LoginQuery;
import com.littleyes.manager.service.AuthService;
import com.littleyes.storage.spi.ValidateCodeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

/**
 * <p> <b> 认证 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-03-16
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Override
    public Map<String, Object> login(LoginQuery query) {
        boolean codeMatches = PluginLoader.of(ValidateCodeRepository.class).load()
                .matches(query.getKey(), query.getCode());
        if (!codeMatches) {
            throw new RuntimeException("验证码错误！");
        }

        return Collections.singletonMap("token", UUID.randomUUID());
    }

}
