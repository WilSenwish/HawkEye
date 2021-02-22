package com.littleyes.collector.dto;

import com.littleyes.common.config.HawkEyeConfig;
import com.littleyes.common.trace.TraceContext;
import com.littleyes.common.util.IpUtils;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * <p> <b>Base DTO【注意，不要随便更换字段顺序，如需要添加字段，append】</b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-19
 */
@Data
public class BaseDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String projectName;
    private String serverIp;
    private String traceId;
    private boolean debug;

    public String generateGroupKey() {
        if (Objects.nonNull(projectName) && Objects.nonNull(serverIp)) {
            return projectName.concat(":").concat(serverIp);
        }

        return null;
    }

    public void initBase() {
        projectName = HawkEyeConfig.getProjectName();
        serverIp    = IpUtils.getLocalIp();
    }

    public void initTrace() {
        traceId = TraceContext.traceId();
        debug   = TraceContext.traceDebugEnabled();
    }

}
