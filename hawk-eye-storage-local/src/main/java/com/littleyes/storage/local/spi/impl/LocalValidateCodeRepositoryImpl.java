package com.littleyes.storage.local.spi.impl;

import com.littleyes.common.core.SPI;
import com.littleyes.common.util.LRUCache;
import com.littleyes.common.util.web.ValidateCode;
import com.littleyes.storage.spi.ValidateCodeRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static com.littleyes.storage.local.util.Constants.HAWK_LOCAL_STORAGE;

/**
 * <p> <b> AccountRepositoryImpl </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-03-16
 */
@Slf4j
@SPI
public class LocalValidateCodeRepositoryImpl implements ValidateCodeRepository {

    private static final int CAPACITY = 1 << 10;

    /**
     * key -> code <br/>
     * <b>only for test or single service usage without data bak</b>
     */
    private static final LRUCache<String> CODES = new LRUCache<>(CAPACITY);

    @Override
    public Map<String, Object> generate() {
        String key = UUID.randomUUID().toString();
        String code = RandomStringUtils.randomNumeric(4);

        Map<String, Object> codeResult = new LinkedHashMap<>();
        codeResult.put("key", key);
        codeResult.put("base64", ValidateCode.generate(code));

        CODES.put(key, code);

        log.info("{} KEY[{}] with CODE [{}]", HAWK_LOCAL_STORAGE, key, code);

        return codeResult;
    }

    @Override
    public boolean matches(String key, String code) {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(code)) {
            return false;
        }

        return code.equalsIgnoreCase(CODES.get(key));
    }

}
