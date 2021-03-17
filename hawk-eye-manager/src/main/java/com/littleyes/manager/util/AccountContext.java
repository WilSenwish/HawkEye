package com.littleyes.manager.util;

import com.littleyes.storage.entity.AccountModel;
import lombok.extern.slf4j.Slf4j;

/**
 * <p> <b>User Context</b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-03-17
 */
@Slf4j
public class AccountContext {

    private static final ThreadLocal<AccountModel> ACCOUNT_CONTEXT = new ThreadLocal<>();

    private AccountContext() {
    }

    public static void set(AccountModel account) {
        ACCOUNT_CONTEXT.set(account);
    }

    public static AccountModel get() {
        return ACCOUNT_CONTEXT.get();
    }

    public static void remove() {
        ACCOUNT_CONTEXT.remove();
    }

}
