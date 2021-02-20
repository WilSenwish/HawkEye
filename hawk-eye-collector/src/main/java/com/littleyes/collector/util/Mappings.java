package com.littleyes.collector.util;

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
        response.getWriter().write("Wanna Hack ME");
    }

}
