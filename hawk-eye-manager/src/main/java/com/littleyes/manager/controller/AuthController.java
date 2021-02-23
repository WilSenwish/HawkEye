package com.littleyes.manager.controller;

import com.littleyes.common.core.PluginLoader;
import com.littleyes.storage.entity.AccountModel;
import com.littleyes.storage.spi.AccountRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p> <b> 认证 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-23
 */
@RestController
@RequestMapping("auth")
public class AuthController {

    @GetMapping("getById")
    public AccountModel getById(@RequestParam Integer id) {
        return PluginLoader.of(AccountRepository.class).load().getById(id);
    }

}
