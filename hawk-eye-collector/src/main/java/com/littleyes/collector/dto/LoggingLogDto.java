package com.littleyes.collector.dto;

import com.littleyes.common.enums.DateTimeFormatterEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * <p> <b>监控日志【注意，不要随便更换字段顺序，如需要添加字段，append】</b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-19
 */
@Data
public class LoggingLogDto extends BaseDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private long timestamp;
    private long time;
    private Integer logLevel;
    private String logLevelStr;
    private String threadName;
    private String className;
    private String methodName;
    private Integer lineNumber;
    private String loggingMessage;

    /**
     * 异常类名称
     */
    private String throwableName;
    /**
     * 异常栈信息
     */
    private String throwableStackTrace;

    @Override
    public void initBase() {
        super.initBase();
        time = DateTimeFormatterEnum.TIME.format(timestamp);
    }

}
