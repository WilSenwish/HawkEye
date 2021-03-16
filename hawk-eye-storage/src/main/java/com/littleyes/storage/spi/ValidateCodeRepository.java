package com.littleyes.storage.spi;

import com.littleyes.common.core.SPI;

import java.util.Map;

/**
 * <p> <b> ValidateCodeRepository </p>
 *
 * @author Junbing.Chen
 * @date 2021-03-16
 */
@SPI
public interface ValidateCodeRepository {

    Map<String, Object> generate();

    boolean matches(String key, String code);

}
