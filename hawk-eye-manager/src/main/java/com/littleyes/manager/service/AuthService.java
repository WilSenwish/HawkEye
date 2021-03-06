package com.littleyes.manager.service;

import com.littleyes.manager.query.LoginQuery;
import com.littleyes.storage.entity.AccountModel;

import java.util.Map;

/**
 * <p> <b> 认证 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-03-16
 */
public interface AuthService {

    Map<String,Object> login(LoginQuery query);

    AccountModel getAccountByToken(String token);

    AccountModel getCurrentAccount();

}
