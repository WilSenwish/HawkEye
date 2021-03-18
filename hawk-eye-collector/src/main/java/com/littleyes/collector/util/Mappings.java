package com.littleyes.collector.util;

import com.littleyes.common.config.HawkEyeConfig;
import com.littleyes.common.util.ApiResponse;
import com.littleyes.common.util.JsonUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static com.littleyes.collector.util.Constants.GIT_COMMIT_ID_KEY;
import static com.littleyes.collector.util.Constants.PROJECT_NAME_KEY;

/**
 * <p> <b> API Mapping </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-20
 */
public class Mappings {

    private static Set<String> MAPPINGS = new HashSet<>(1 << 10);

    public static void fill(Collection<String> mappings) {
        MAPPINGS.addAll(mappings);
    }

    public static boolean include(String event) {
        return MAPPINGS.contains(event);
    }

    public static boolean exclude(String event) {
        return !include(event);
    }

    public static void hack(HttpServletResponse response) throws IOException {
        response.addHeader(GIT_COMMIT_ID_KEY, HawkEyeConfig.getGitCommitId());
        response.addHeader(PROJECT_NAME_KEY, HawkEyeConfig.getProjectName());

        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        response.getWriter().write(JsonUtils.toString(ApiResponse.failure("(:-P) Wanna Hack ME (:-P)")));
    }

}
