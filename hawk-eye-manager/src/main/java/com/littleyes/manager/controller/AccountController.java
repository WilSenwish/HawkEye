package com.littleyes.manager.controller;

import com.littleyes.manager.service.AuthService;
import com.littleyes.storage.entity.AccountModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p> <b> 账户 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-03-17
 */
@Slf4j
@RestController
@RequestMapping("account")
public class AccountController {

    @Autowired
    private AuthService authService;

    @GetMapping("current")
    public AccountModel getCurrentAccount() {
        return authService.getCurrentAccount();
    }

}
