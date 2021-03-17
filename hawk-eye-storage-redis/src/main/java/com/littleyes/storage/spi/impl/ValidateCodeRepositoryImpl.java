package com.littleyes.storage.spi.impl;

import com.littleyes.common.core.SPI;
import com.littleyes.common.util.LRUCache;
import com.littleyes.storage.spi.ValidateCodeRepository;
import com.littleyes.storage.util.ValidateCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static com.littleyes.storage.util.Constants.HAWK_REDIS_STORAGE;

/**
 * <p> <b> AccountRepositoryImpl </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-03-16
 */
@Slf4j
@SPI
public class ValidateCodeRepositoryImpl implements ValidateCodeRepository {

    private static final int CAPACITY = 1 << 10;

    private static final LRUCache CODES = new LRUCache(CAPACITY);

    @Override
    public Map<String, Object> generate() {
        String key = UUID.randomUUID().toString();
        String code = RandomStringUtils.randomNumeric(4);

        Map<String, Object> codeResult = new LinkedHashMap<>();
        codeResult.put("key", key);
        codeResult.put("base64", ValidateCode.generate(code));

        synchronized (CODES) {
            CODES.put(key, code);
        }

        log.info("{} KEY[{}] with CODE [{}]", HAWK_REDIS_STORAGE, key, code);

        return codeResult;
    }

    @Override
    public boolean matches(String key, String code) {
        return StringUtils.isNotBlank(key) && StringUtils.isNotBlank(code)
                && code.equalsIgnoreCase(CODES.get(key));
    }

}
