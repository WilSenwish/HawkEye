package com.littleyes.collector.util;

import com.littleyes.common.util.JsonUtils;
import com.littleyes.common.util.web.ApiResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * <p> <b> API Mapping </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-20
 */
public class Mappings {

    private static final Set<String> MAPPINGS = new HashSet<>(1 << 10);

    public static void fill(Collection<String> mappings) {
        MAPPINGS.addAll(mappings);
    }

    public static boolean include(String event) {
        // TODO 匹配 PathVar
        return MAPPINGS.contains(event);
    }

    public static boolean exclude(String event) {
        return !include(event);
    }

    public static void hack(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        response.getWriter().write(JsonUtils.toString(ApiResponse.failure("(:-P) Wanna Hack ME (:-P)")));
    }

}
