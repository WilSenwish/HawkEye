package com.littleyes.storage.mapper;

import com.littleyes.storage.entity.AccountModel;

/**
 * <p> <b> AccountMapper </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-23
 */
public interface AccountMapper {

    AccountModel selectById(Integer id);

}
