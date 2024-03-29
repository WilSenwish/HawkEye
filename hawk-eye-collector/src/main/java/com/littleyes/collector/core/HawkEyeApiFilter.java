package com.littleyes.collector.core;

import com.littleyes.collector.buf.PerformanceLogBuffer;
import com.littleyes.collector.logging.HawkEyeMdc;
import com.littleyes.collector.sample.HawkEyeSampleConfig;
import com.littleyes.collector.sample.HawkEyeSampleDecisionManager;
import com.littleyes.collector.util.Mappings;
import com.littleyes.collector.util.PerformanceLogBuilder;
import com.littleyes.common.config.HawkEyeConfig;
import com.littleyes.common.dto.PerformanceLogDto;
import com.littleyes.common.enums.PerformanceTypeEnum;
import com.littleyes.common.trace.TraceContext;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import static com.littleyes.collector.util.Constants.DEFAULT_DEBUG_MARKER_KEY;
import static com.littleyes.collector.util.Constants.GIT_COMMIT_ID_KEY;
import static com.littleyes.collector.util.Constants.HAWK_EYE_COLLECTOR;
import static com.littleyes.collector.util.Constants.OPTIONS_METHOD;
import static com.littleyes.collector.util.Constants.PROJECT_NAME_KEY;
import static com.littleyes.collector.util.Constants.TRACE_ID_KEY;

/**
 * <p> <b> Api Filter </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-20
 */
@Slf4j
public class HawkEyeApiFilter implements Filter {

    private static String debugMarker = HawkEyeSampleConfig.getInstance()
            .getProperty("debugSample", DEFAULT_DEBUG_MARKER_KEY);

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

        log.info("{} Current [{}] is [{}]", HAWK_EYE_COLLECTOR, GIT_COMMIT_ID_KEY, HawkEyeConfig.getGitCommitId());
        log.info("{} Current [{}] is [{}]", HAWK_EYE_COLLECTOR, PROJECT_NAME_KEY, HawkEyeConfig.getProjectName());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            HttpServletRequest req = (HttpServletRequest) request;
            HttpServletResponse res = (HttpServletResponse) response;

            try {
                String uri = req.getRequestURI();

                if (Mappings.exclude(uri)) {
                    Mappings.hack(res);
                    return;
                }

                if (isNotOptions(req) && isIncludePath(uri)) {
                    this.doFilterInternal(req, res, chain);
                } else {
                    chain.doFilter(request, response);
                }
            } finally {
                res.addHeader(GIT_COMMIT_ID_KEY, HawkEyeConfig.getGitCommitId());
                res.addHeader(PROJECT_NAME_KEY, HawkEyeConfig.getProjectName());
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
            if (HawkEyeConfig.isPerformanceEnabled()) {
                PerformanceLogDto performanceLog = PerformanceLogBuilder.build(
                        req.getRequestURI(),
                        req.getMethod(),
                        PerformanceTypeEnum.API,
                        success,
                        start,
                        System.currentTimeMillis()
                );
                PerformanceLogBuffer.sample(performanceLog);
            }
        }
    }

    private void initTraceContext(HttpServletRequest req, HttpServletResponse res) {
        TraceContext context = TraceContext.init(extractTraceId(req), extractTraceDebugSwitch(req));
        HawkEyeSampleDecisionManager.preDecide(context);

        res.addHeader(TRACE_ID_KEY, context.getTraceId());

        HawkEyeMdc.put(context.getTraceId());
    }

    private String extractTraceId(HttpServletRequest req) {
        String traceId = req.getHeader(TRACE_ID_KEY);
        if (Objects.isNull(traceId)) {
            traceId = req.getParameter(TRACE_ID_KEY);
        }

        return traceId;
    }

    private boolean extractTraceDebugSwitch(HttpServletRequest req) {
        String traceDebugSwitch = req.getHeader(debugMarker);
        if (Objects.isNull(traceDebugSwitch)) {
            traceDebugSwitch = req.getParameter(debugMarker);
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
