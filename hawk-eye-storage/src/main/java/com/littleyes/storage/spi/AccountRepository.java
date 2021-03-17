package com.littleyes.storage.spi;

import com.littleyes.common.core.SPI;
import com.littleyes.storage.entity.AccountModel;

/**
 * <p> <b> AccountRepository </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-23
 */
@SPI
public interface AccountRepository {

    AccountModel selectByUsernameAndPassword(String username, String password);

}
