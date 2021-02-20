package com.littleyes.agent.core.logging.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.util.InterruptUtil;
import com.littleyes.agent.core.logging.Constants;
import com.littleyes.collector.dto.LoggingLogDto;
import com.littleyes.collector.worker.HawkEyeLoggingCollector;
import com.littleyes.common.config.HawkEyeConfig;
import com.littleyes.common.trace.TraceContext;
import com.littleyes.common.util.JsonUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static com.littleyes.agent.core.logging.Constants.BUFFER_MAX_CAPACITY;
import static com.littleyes.agent.core.logging.Constants.HAWK_EYE_AGENT;

/**
 * <p> <b> Logback 日志 Appender </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-19
 */
public class LogbackAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    private static final int STACK_MAX_LEVEL = 2048;
    private static final String CAUSED_BY_MARK = "#Caused by: #";

    private static final BlockingQueue<LoggingLogDto> BUFFER = new ArrayBlockingQueue<>(BUFFER_MAX_CAPACITY);

    private static HawkEyeLoggingCollector hawkEyeLoggingCollector;
    private static final long COLLECTOR_MAX_FLUSH_TIME = 2000L;

    private static Level logCollectLevel = Level.toLevel(HawkEyeConfig.getLoggingCollectLevel(), Level.INFO);
    private static boolean isLoggingDisabled() {
        return HawkEyeConfig.isLoggingDisabled();
    }

    @Override
    public synchronized void start() {
        if (isLoggingDisabled()) {
            addInfo(HAWK_EYE_AGENT + " Logging disabled, no need to start LogbackAppender.");
            return;
        }

        if (isStarted()) {
            addInfo(HAWK_EYE_AGENT + " Already started, no need to restart LogbackAppender.");
            return;
        }

        addInfo(HAWK_EYE_AGENT + " LogbackAppender Starting...");

        if (Objects.isNull(hawkEyeLoggingCollector)) {
            hawkEyeLoggingCollector = new HawkEyeLoggingCollector(BUFFER);
        }

        super.start();
        hawkEyeLoggingCollector.start();

        addInfo(HAWK_EYE_AGENT + " LogbackAppender Started.");
    }

    @Override
    public synchronized void stop() {
        if (isLoggingDisabled()) {
            addInfo(HAWK_EYE_AGENT + " Logging disabled, no started LogbackAppender.");
            return;
        }

        if (!isStarted()) {
            addInfo(HAWK_EYE_AGENT + " Already stopped, no started LogbackAppender.");
            return;
        }

        addInfo(HAWK_EYE_AGENT + " LogbackAppender Stopping...");

        // mark this appender as stopped so that Worker can also processPriorToRemoval if it is invoking
        // and sub-appenders consume the interruption
        super.stop();

        if (Objects.nonNull(hawkEyeLoggingCollector)) {
            // interrupt the runner thread so that it can terminate. Note that the interruption can be consumed
            // by sub-appenders
            hawkEyeLoggingCollector.interrupt();

            InterruptUtil interruptUtil = new InterruptUtil(context);
            try {
                interruptUtil.maskInterruptFlag();
                hawkEyeLoggingCollector.join(COLLECTOR_MAX_FLUSH_TIME);

                // check to see if the thread ended and if not add a warning message
                if (hawkEyeLoggingCollector.isAlive()) {
                    addWarn(HAWK_EYE_AGENT + " Max queue flush timeout ("
                            + COLLECTOR_MAX_FLUSH_TIME + " ms) exceeded. Approximately "
                            + BUFFER.size() + " queued events were possibly discarded!!!");
                } else {
                    addInfo(HAWK_EYE_AGENT + " Queue flush finished successfully within timeout.");
                }
            } catch (InterruptedException e) {
                int remaining = BUFFER.size();
                addError(HAWK_EYE_AGENT + " Failed to join runner thread. "
                        + remaining + " queued events may be discarded!!!", e);
            } finally {
                interruptUtil.unmaskInterruptFlag();
            }
        }

        addInfo(HAWK_EYE_AGENT + " LogbackAppender Stopped.");
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        if (isLoggingDisabled()) {
            return;
        }

        if (Objects.isNull(hawkEyeLoggingCollector) || !hawkEyeLoggingCollector.isAlive()) {
            addError(HAWK_EYE_AGENT + " " + hawkEyeLoggingCollector.getName() + " not started or died!!!");
            return;
        }

        if (Objects.isNull(eventObject) || ArrayUtils.isEmpty(eventObject.getCallerData())) {
            return;
        }

        if (!eventObject.getLevel().isGreaterOrEqual(logCollectLevel)) {
            return;
        }

        try {
            LoggingLogDto loggingLog = buildLoggingLog(eventObject);
            boolean success = BUFFER.offer(loggingLog);
            if (!success) {
                addWarn(HAWK_EYE_AGENT + " Logging log [" + JsonUtils.toString(loggingLog) + "] queue failed!!!");
            }
        } catch (Exception ignore) {
        }
    }

    private LoggingLogDto buildLoggingLog(ILoggingEvent event) {
        LoggingLogDto loggingLog = new LoggingLogDto();

        // 链路信息
        loggingLog.setTraceId(TraceContext.traceId());

        // Log 本身信息
        loggingLog.setTimestamp(event.getTimeStamp());
        loggingLog.setLogLevel(event.getLevel().toInt());
        loggingLog.setLogLevelStr(event.getLevel().toString());
        loggingLog.setThreadName(Thread.currentThread().getName());

        // 日志发生坐标信息
        StackTraceElement stackTrace = getLastStackTrace(event);
        loggingLog.setClassName(stackTrace.getClassName());
        loggingLog.setMethodName(stackTrace.getMethodName());
        loggingLog.setLineNumber(stackTrace.getLineNumber());
        loggingLog.setLoggingMessage(extractLoggingMessage(event));

        // 异常信息
        IThrowableProxy throwableProxy = event.getThrowableProxy();
        if (Objects.nonNull(throwableProxy)) {
            StringBuilder throwableStackTrace = new StringBuilder();
            recursivelyAppendStackTrace(throwableStackTrace, throwableProxy, null);
            loggingLog.setThrowableName(throwableProxy.getClassName());
            loggingLog.setThrowableStackTrace(throwableStackTrace.toString());
        }

        return loggingLog;
    }

    private static StackTraceElement getLastStackTrace(ILoggingEvent event) {
        return event.getCallerData()[0];
    }

    private static String extractLoggingMessage(ILoggingEvent event) {
        return Objects.isNull(event.getMessage()) ? StringUtils.EMPTY : event.getFormattedMessage();
    }

    private void recursivelyAppendStackTrace(StringBuilder throwableStackTrace,
                                             IThrowableProxy throwableProxy,
                                             String prefix) {

        if (Objects.isNull(throwableProxy)) {
            return;
        }

        if (Objects.nonNull(prefix)) {
            throwableStackTrace.append(prefix);
        }

        throwableStackTrace.append(throwableProxy.getClassName()).append(": ").append(throwableProxy.getMessage());

        StackTraceElementProxy[] stackTraceArr = throwableProxy.getStackTraceElementProxyArray();
        if (ArrayUtils.isNotEmpty(stackTraceArr)) {
            int maxLevel = STACK_MAX_LEVEL > stackTraceArr.length ? stackTraceArr.length : STACK_MAX_LEVEL;
            for (int i = 0; i < maxLevel; i++) {
                throwableStackTrace.append(Constants.MONITOR_SEPARATOR).append(stackTraceArr[i].getSTEAsString());
            }
        }

        IThrowableProxy cause = throwableProxy.getCause();
        if (Objects.nonNull(cause)) {
            recursivelyAppendStackTrace(throwableStackTrace, cause, CAUSED_BY_MARK);
        }
    }

}
