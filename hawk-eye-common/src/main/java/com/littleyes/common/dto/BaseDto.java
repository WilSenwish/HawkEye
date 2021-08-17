package com.littleyes.common.dto;

import com.littleyes.common.config.HawkEyeConfig;
import com.littleyes.common.trace.TraceContext;
import com.littleyes.common.util.IpUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

/**
 * <p> <b>Base DTO【注意，不要随便更换字段顺序，如需要添加字段，append】</b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-19
 */
@EqualsAndHashCode
@Getter
@Setter
public class BaseDto implements Serializable {

    private static final long serialVersionUID = 147L;

    private String  projectName;
    private String  serverIp;
    private Integer serverPort;
    private String  threadName;
    private String  traceId;

    /**
     * 项目-服务IP-服务PORT 分组 KEY
     *
     * @return projectName:serverIp:serverPort
     */
    public String groupKey() {
        if (Objects.nonNull(projectName) && Objects.nonNull(serverIp) && Objects.nonNull(serverPort)) {
            return projectName.concat(":").concat(serverIp).concat(":").concat(serverPort.toString());
        }

        return null;
    }

    public void initBase() {
        projectName = HawkEyeConfig.getProjectName();
        serverIp    = IpUtils.getLocalIp();
        serverPort  = HawkEyeConfig.getServerPort();
    }

    public void initTrace() {
        threadName  = Thread.currentThread().getName();
        traceId     = TraceContext.traceId();
    }

}
