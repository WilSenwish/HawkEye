package com.littleyes.common.enums;

/**
 * <p> <b> 耗时范围枚举 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-18
 */
public enum TimeRangeEnum {

    /**
     * 耗时范围 0-100MS
     */
    TIME_LESS_100MS     ("tcr0", "0-100MS",      0L,    100L),
    /**
     * 耗时范围 100MS-200MS
     */
    TIME_100MS_200MS    ("tcr1", "100MS-200MS",  100L,  200L),
    /**
     * 耗时范围 200MS-500MS
     */
    TIME_200MS_500MS    ("tcr2", "200MS-500MS",  200L,  500L),
    /**
     * 耗时范围 500MS-1000MS
     */
    TIME_500MS_1000MS   ("tcr3", "500MS-1000MS", 500L,  1000L),
    /**
     * 耗时范围 1S-2S
     */
    TIME_1000MS_2000MS  ("tcr4", "1S-2S",        1000L, 2000L),
    /**
     * 耗时范围 2S-5S
     */
    TIME_2000MS_5000MS  ("tcr5", "2S-5S",        2000L, 5000L),
    /**
     * 耗时范围 >=5S
     */
    TIME_GREATER_5000MS ("tcr6", ">=5S",         5000L, Long.MAX_VALUE),
    ;

    private final String key;
    private final String name;
    private final long minInclude;
    private final long maxExclude;

    TimeRangeEnum(String key, String name, long minInclude, long maxExclude) {
        this.key = key;
        this.name = name;
        this.minInclude = minInclude;
        this.maxExclude = maxExclude;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public static TimeRangeEnum classify(long timeConsume) {
        for (TimeRangeEnum range : TimeRangeEnum.values()) {
            if (timeConsume >= range.minInclude && timeConsume < range.maxExclude) {
                return range;
            }
        }

        return TIME_GREATER_5000MS;
    }

}
