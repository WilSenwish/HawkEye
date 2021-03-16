package com.littleyes.storage.spi.impl;

import com.littleyes.common.core.SPI;
import com.littleyes.storage.spi.ValidateCodeRepository;
import com.littleyes.storage.util.ValidateCode;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * <p> <b> AccountRepositoryImpl </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-03-16
 */
@SPI
public class ValidateCodeRepositoryImpl implements ValidateCodeRepository {

    private static final int CAPACITY = 1 << 10;

    private static final ValidateCodeCache CODES = new ValidateCodeCache(CAPACITY);

    /**
     * <p> <b> Only for Test or Single Service Usage </b> </p>
     *
     * @author Junbing.Chen
     * @date 2021-03-16
     */
    private static class ValidateCodeCache extends LinkedHashMap<String, String> {

        ValidateCodeCache(int initialCapacity) {
            super(initialCapacity, 1F, true);
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
            return size() > CAPACITY;
        }
    }

    @Override
    public Map<String, Object> generate() {
        String key = UUID.randomUUID().toString();
        String code = RandomStringUtils.randomNumeric(4);

        synchronized (CODES) {
            CODES.put(key, code);
        }

        Map<String, Object> codeResult = new LinkedHashMap<>();
        codeResult.put("key", key);
        codeResult.put("base64", ValidateCode.generate(code));

        return codeResult;
    }

    @Override
    public boolean matches(String key, String code) {
        return StringUtils.isNotBlank(key) && StringUtils.isNotBlank(code)
                && code.equalsIgnoreCase(CODES.get(key));
    }

}
