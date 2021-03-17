package com.littleyes.manager.service.impl;

import com.littleyes.common.core.PluginLoader;
import com.littleyes.common.util.ApiException;
import com.littleyes.common.util.LRUCache;
import com.littleyes.manager.query.LoginQuery;
import com.littleyes.manager.service.AuthService;
import com.littleyes.manager.util.AccountContext;
import com.littleyes.storage.entity.AccountModel;
import com.littleyes.storage.spi.AccountRepository;
import com.littleyes.storage.spi.ValidateCodeRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
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

    private static final int CAPACITY = 1 << 10;

    /**
     * token -> account <br/>
     * <b>only for test or single service usage without data bak</b>
     */
    private static final LRUCache<AccountModel> SESSIONS = new LRUCache<>(CAPACITY);

    @Override
    public Map<String, Object> login(LoginQuery query) {
        if (StringUtils.isBlank(query.getUsername()) || StringUtils.isBlank(query.getPassword())) {
            throw new ApiException("用户名密码不能为空！");
        }
        if (StringUtils.isBlank(query.getKey()) || StringUtils.isBlank(query.getCode())) {
            throw new ApiException("验证码不能为空！");
        }

        ValidateCodeRepository vcr = PluginLoader.of(ValidateCodeRepository.class).load();
        if (!vcr.matches(query.getKey(), query.getCode())) {
            throw new ApiException("验证码已过期或错误！");
        }

        AccountRepository accountRepository = PluginLoader.of(AccountRepository.class).load();
        AccountModel account = accountRepository
                .selectByUsernameAndPassword(query.getUsername(), query.getPassword());
        if (Objects.isNull(account)) {
            throw new ApiException("用户名密码错误！");
        }

        String token = UUID.randomUUID().toString();
        synchronized (SESSIONS) {
            SESSIONS.put(token, account);
        }

        return Collections.singletonMap("token", token);
    }

    @Override
    public AccountModel getAccountByToken(String token) {
        AccountModel account;

        synchronized (SESSIONS) {
            account = SESSIONS.get(token);
        }

        return account;
    }

    @Override
    public AccountModel getCurrentAccount() {
        return AccountContext.get();
    }

}
