package com.littleyes.collector.core;

import com.littleyes.collector.buf.PerformanceLogBuffer;
import com.littleyes.collector.logging.HawkEyeMdc;
import com.littleyes.collector.sample.HawkEyeSampleConfig;
import com.littleyes.collector.sample.HawkEyeSampleDecisionManager;
import com.littleyes.collector.util.Mappings;
import com.littleyes.common.config.HawkEyeConfig;
import com.littleyes.common.enums.PerformanceTypeEnum;
import com.littleyes.common.trace.TraceContext;
import com.littleyes.common.util.PerformanceContext;
import com.littleyes.common.util.RequestParamUtils;
import com.littleyes.threadpool.util.HawkEyeExecutors;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import static com.littleyes.collector.util.Constants.*;

/**
 * <p> <b> Api Filter </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-20
 */
@Slf4j
public class HawkEyeApiFilter implements Filter {

    private static ExecutorService sampler = HawkEyeExecutors.newThreadExecutor("Sampler", 16);
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
                PerformanceContext context = PerformanceContext.init(
                        req.getRequestURI(),
                        req.getMethod(),
                        PerformanceTypeEnum.API.getType(),
                        success,
                        start,
                        System.currentTimeMillis()
                );
                context.setParameters(RequestParamUtils.map(req));
                TraceContext.append(PerformanceTypeEnum.API.getType());

                sampler.execute(new SampleRunnable());
            }
        }
    }

    private void initTraceContext(HttpServletRequest req, HttpServletResponse res) {
        TraceContext context = TraceContext.init(extractTraceId(req), extractTraceDebugSwitch(req));

        res.addHeader(TRACE_ID_KEY, context.getTraceId());
        res.addHeader(GIT_COMMIT_ID_KEY, HawkEyeConfig.getGitCommitId());
        res.addHeader(PROJECT_NAME_KEY, HawkEyeConfig.getProjectName());

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

    /**
     * 异步采样任务
     */
    static class SampleRunnable implements Runnable {

        private final TraceContext traceContext;

        SampleRunnable() {
            this.traceContext = TraceContext.get();
            TraceContext.remove();
        }

        @Override
        public void run() {
            if (HawkEyeSampleDecisionManager.decide(traceContext)) {
                PerformanceLogBuffer.sample(traceContext.getPerformanceLogs());
            }
        }
    }

}
