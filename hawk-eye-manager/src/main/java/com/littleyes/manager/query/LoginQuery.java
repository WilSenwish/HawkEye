package com.littleyes.manager.query;

import lombok.Data;

/**
 * <p> <b> 登录参数 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-03-16
 */
@Data
public class LoginQuery {

    private String username;
    private String password;
    private String key;
    private String code;

}
