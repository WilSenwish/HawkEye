package com.littleyes.common.enums;

/**
 * <p> <b> 性能类型枚举 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-18
 */
public enum PerformanceTypeEnum {

    /**
     * 接口性能日志
     */
    API     (1),
    /**
     * MYSQL操作性能日志
     */
    MYSQL   (2),
    /**
     * REDIS操作性能日志
     */
    REDIS   (3),
    /**
     * RestTemplate操作性能日志
     */
    HTTP    (4),
    ;

    private final int type;

    PerformanceTypeEnum(int type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

}
