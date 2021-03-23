package com.littleyes.agent.core.logging.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import com.littleyes.agent.core.util.Constants;
import com.littleyes.collector.buf.LoggingLogBuffer;
import com.littleyes.common.dto.LoggingLogDto;
import com.littleyes.common.config.HawkEyeConfig;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

import static com.littleyes.agent.core.util.Constants.HAWK_EYE_AGENT;

/**
 * <p> <b> Logback 日志 Appender </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-19
 */
public class LogbackAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    private static final int STACK_MAX_LEVEL = 2048;
    private static final String CAUSED_BY_MARK = "#Caused by: #";

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
        super.start();
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
        super.stop();
        addInfo(HAWK_EYE_AGENT + " LogbackAppender Stopped.");
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        if (isLoggingDisabled()) {
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
            LoggingLogBuffer.log(loggingLog);
        } catch (Exception ignore) {
        }
    }

    private LoggingLogDto buildLoggingLog(ILoggingEvent event) {
        StackTraceElement stackTrace = getLastStackTrace(event);
        String throwableName = null;
        String throwableStackTrace = null;
        IThrowableProxy throwableProxy = event.getThrowableProxy();
        if (Objects.nonNull(throwableProxy)) {
            StringBuilder throwableStackTraceBuf = new StringBuilder();
            recursivelyAppendStackTrace(throwableStackTraceBuf, throwableProxy, null);
            throwableName = throwableProxy.getClassName();
            throwableStackTrace = throwableStackTraceBuf.toString();
        }

        return LoggingLogDto.builder()
                // Log 本身信息
                .timestamp(event.getTimeStamp())
                .logLevel(event.getLevel().toInt())
                .logLevelStr(event.getLevel().toString())
                // 日志发生坐标信息
                .className(stackTrace.getClassName())
                .methodName(stackTrace.getMethodName())
                .lineNumber(stackTrace.getLineNumber())
                .loggingMessage(extractLoggingMessage(event))
                // 异常信息
                .throwableName(throwableName)
                .throwableStackTrace(throwableStackTrace)
                .build();
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
