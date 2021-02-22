package com.littleyes.common.enums;

import org.apache.commons.lang3.time.FastDateFormat;

import java.util.Date;
import java.util.Objects;

/**
 * <p> <b> 日期时间格式化工具类 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-22
 */
public enum DateTimeFormatterEnum {

    /**
     * 具体到毫秒的时间
     */
    TIME(FastDateFormat.getInstance("yyyyMMddHHmmssSSS")),
    /**
     * 分钟
     */
    MINUTE(FastDateFormat.getInstance("yyyyMMddHHmm")),
    ;

    private final FastDateFormat format;

    DateTimeFormatterEnum(FastDateFormat format) {
        this.format = format;
    }

    public long format(long timestamp) {
        return Long.parseLong(format.format(timestamp));
    }

    public long format(Date date) {
        if (Objects.isNull(date)) {
            return 0L;
        }

        return format(date.getTime());
    }

}
