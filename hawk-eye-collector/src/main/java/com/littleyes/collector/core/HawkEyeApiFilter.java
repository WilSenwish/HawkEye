package com.littleyes.collector.core;

import com.littleyes.common.config.HawkEyeConfig;
import com.littleyes.common.trace.TraceContext;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import static com.littleyes.collector.util.Constants.*;

/**
 * <p> <b> Api Filter </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-20
 */
public class HawkEyeApiFilter implements Filter {

    private final Set<String> excludeUrls       = new LinkedHashSet<>();
    private final Set<String> excludePrefixes   = new LinkedHashSet<>();
    private final Set<String> excludeSuffixes   = new LinkedHashSet<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String configuredExcludeUrls = filterConfig.getInitParameter("excludeUrls");
        String configuredExcludeSuffixes = filterConfig.getInitParameter("excludeSuffixes");
        String configuredExcludePrefixes = filterConfig.getInitParameter("excludePrefixes");

        if (configuredExcludeUrls != null) {
            this.excludeUrls.addAll(Arrays.asList(configuredExcludeUrls.split(",")));
        }
        if (configuredExcludeSuffixes != null) {
            this.excludeSuffixes.addAll(Arrays.asList(configuredExcludeSuffixes.split(",")));
        }
        if (configuredExcludePrefixes != null) {
            this.excludePrefixes.addAll(Arrays.asList(configuredExcludePrefixes.split(",")));
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            HttpServletRequest req = (HttpServletRequest) request;
            HttpServletResponse res = (HttpServletResponse) response;
            String uri = req.getRequestURI();

            if (HawkEyeConfig.isPerformanceEnabled() && isNotOptions(req) && isIncludePath(uri)) {
                this.doFilterInternal(req, res, chain);
            } else {
                chain.doFilter(request, response);
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    private void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        long start = System.currentTimeMillis();
        boolean success = false;

        try {
            initTraceContext(req, res);
            chain.doFilter(req, res);
            success = true;
        } finally {
            // TODO
            System.out.println(req.getRequestURL() + " " + success + " cost " + (System.currentTimeMillis() - start));
        }
    }

    private void initTraceContext(HttpServletRequest req, HttpServletResponse res) {
        TraceContext context = TraceContext.init(extractTraceId(req), extractTraceDebugSwitch(req));

        res.addHeader(TRACE_ID_KEY, context.getTraceId());
        res.addHeader(GIT_COMMIT_ID_KEY, HawkEyeConfig.getCommitId());
    }

    private String extractTraceId(HttpServletRequest req) {
        String traceId = req.getHeader(TRACE_ID_KEY);
        if (Objects.isNull(traceId)) {
            traceId = req.getParameter(TRACE_ID_KEY);
        }

        return traceId;
    }

    private boolean extractTraceDebugSwitch(HttpServletRequest req) {
        String traceDebugSwitch = req.getHeader(TRACE_DEBUG_KEY);
        if (Objects.isNull(traceDebugSwitch)) {
            traceDebugSwitch = req.getParameter(TRACE_DEBUG_KEY);
        }

        return Boolean.parseBoolean(traceDebugSwitch);
    }

    private boolean isExcludePath(String path) {
        try {
            boolean exclude = this.excludeUrls.contains(path);

            if (!exclude) {
                for (String suffix : this.excludeSuffixes) {
                    if (path.endsWith(suffix)) {
                        exclude = true;
                        break;
                    }
                }
            }

            if (!exclude) {
                for (String prefix : this.excludePrefixes) {
                    if (path.startsWith(prefix)) {
                        exclude = true;
                        break;
                    }
                }
            }

            return exclude;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isIncludePath(String path) {
        return !isExcludePath(path);
    }

    private boolean isNotOptions(HttpServletRequest req) {
        return !OPTIONS_METHOD.equalsIgnoreCase(req.getMethod());
    }

    @Override
    public void destroy() {
    }

}
