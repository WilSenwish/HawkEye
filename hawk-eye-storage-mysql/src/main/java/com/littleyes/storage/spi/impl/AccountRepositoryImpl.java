package com.littleyes.storage.spi.impl;

import com.littleyes.common.core.SPI;
import com.littleyes.common.util.SpringContextHolder;
import com.littleyes.storage.entity.AccountModel;
import com.littleyes.storage.mapper.AccountMapper;
import com.littleyes.storage.spi.AccountRepository;

/**
 * <p> <b> AccountRepositoryImpl </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-23
 */
@SPI
public class AccountRepositoryImpl implements AccountRepository {

    @Override
    public AccountModel getById(Integer id) {
        return SpringContextHolder.getBean(AccountMapper.class).selectById(id);
    }

}
