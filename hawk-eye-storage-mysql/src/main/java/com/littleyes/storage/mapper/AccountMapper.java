package com.littleyes.storage.mapper;

import com.littleyes.storage.entity.AccountModel;
import org.apache.ibatis.annotations.Param;

/**
 * <p> <b> AccountMapper </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-23
 */
public interface AccountMapper {

    AccountModel selectByUsernameAndPassword(@Param("username") String username,
                                             @Param("password") String password);

}
