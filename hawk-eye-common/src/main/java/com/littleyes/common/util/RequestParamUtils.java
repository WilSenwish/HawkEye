package com.littleyes.common.util;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p> <b> 请求参数工具类 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-22
 */
public class RequestParamUtils {

    private RequestParamUtils() {
    }

    public static String json(HttpServletRequest request) {
        return JsonUtils.toString(map(request));
    }

    public static Map<String, Object> map(HttpServletRequest request) {
        Map<String, String[]> paramMap = request.getParameterMap();
        if (paramMap.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, Object> param = new LinkedHashMap<>(paramMap.size());

        for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
            param.put(entry.getKey(), entry.getValue()[0]);
        }

        return param;
    }

}
