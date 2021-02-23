package com.littleyes.storage.entity;

import lombok.Data;

/**
 * <p> <b> Account </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-23
 */
@Data
public class AccountModel {
    private Integer id;
    private String username;
    private String password;
}
