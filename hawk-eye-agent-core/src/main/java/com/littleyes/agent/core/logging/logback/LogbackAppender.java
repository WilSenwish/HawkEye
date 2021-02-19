package com.littleyes.agent.core.logging.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.util.InterruptUtil;
import com.littleyes.agent.core.logging.Constants;
import com.littleyes.collector.dto.LoggingLogDto;
import com.littleyes.collector.worker.LoggingCollectWorker;
import com.littleyes.common.config.HawkEyeConfig;
import com.littleyes.common.trace.TraceContext;
import com.littleyes.common.util.JsonUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static com.littleyes.agent.core.logging.Constants.BUFFER_QUEUE_MAX_CAPACITY;

/**
 * <p> <b>Logback 日志 Appender</b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-19
 */
public class LogbackAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    private static final int STACK_MAX_LEVEL = 2048;
    private static final String CAUSED_BY_MARK = "#Caused by: #";

    private static final BlockingQueue<LoggingLogDto> BUFFER_QUEUE = new ArrayBlockingQueue<>(BUFFER_QUEUE_MAX_CAPACITY);

    private static LoggingCollectWorker loggingCollectWorker;
    private static final long COLLECTOR_MAX_FLUSH_TIME = 2000L;

    private static Level logCollectLevel = Level.toLevel(HawkEyeConfig.getLoggingCollectLevel(), Level.INFO);
    private static boolean isLoggingDisabled() {
        return HawkEyeConfig.isLoggingDisabled();
    }

    @Override
    public synchronized void start() {
        if (isLoggingDisabled()) {
            addInfo("Logging disabled, no need to start LogbackAppender.");
            return;
        }

        if (isStarted()) {
            addInfo("Already started, no need to restart LogbackAppender.");
            return;
        }

        addInfo("LogbackAppender Starting...");

        if (Objects.isNull(loggingCollectWorker)) {
            loggingCollectWorker = new LoggingCollectWorker(BUFFER_QUEUE);
            loggingCollectWorker.setName("HawkEyeLoggingCollectWorker");
        }

        super.start();
        loggingCollectWorker.start();

        addInfo("LogbackAppender Started.");
    }

    @Override
    public synchronized void stop() {
        if (isLoggingDisabled()) {
            addInfo("Logging disabled, no started LogbackAppender.");
            return;
        }

        if (!isStarted()) {
            addInfo("Already stopped, no started LogbackAppender.");
            return;
        }

        addInfo("LogbackAppender Stopping...");

        // mark this appender as stopped so that Worker can also processPriorToRemoval if it is invoking
        // and sub-appenders consume the interruption
        super.stop();

        // interrupt the runner thread so that it can terminate. Note that the interruption can be consumed
        // by sub-appenders
        loggingCollectWorker.interrupt();

        InterruptUtil interruptUtil = new InterruptUtil(context);
        try {
            interruptUtil.maskInterruptFlag();
            loggingCollectWorker.join(COLLECTOR_MAX_FLUSH_TIME);

            // check to see if the thread ended and if not add a warning message
            if (loggingCollectWorker.isAlive()) {
                addWarn("Max queue flush timeout (" + COLLECTOR_MAX_FLUSH_TIME + " ms) exceeded. Approximately "
                        + BUFFER_QUEUE.size() + " queued events were possibly discarded!!!");
            } else {
                addInfo("Queue flush finished successfully within timeout.");
            }
        } catch (InterruptedException e) {
            int remaining = BUFFER_QUEUE.size();
            addError("Failed to join runner thread. " + remaining + " queued events may be discarded!!!", e);
        } finally {
            interruptUtil.unmaskInterruptFlag();
        }

        addInfo("LogbackAppender Stopped.");
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        if (isLoggingDisabled()) {
            return;
        }

        if (!loggingCollectWorker.isAlive()) {
            addError("!!!" + loggingCollectWorker.getName() + " died!!!");
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
            boolean success = BUFFER_QUEUE.offer(loggingLog);
            if (!success) {
                addWarn("logging log [" + JsonUtils.toString(loggingLog) + "] queue failed!!!");
            }
        } catch (Exception ignore) {
        }
    }

    private LoggingLogDto buildLoggingLog(ILoggingEvent event) {
        LoggingLogDto loggingLog = new LoggingLogDto();

        // Log 本身信息
        loggingLog.setTimestamp(event.getTimeStamp());
        // TODO format: 20210219175858001
        loggingLog.setTime(loggingLog.getTimestamp());
        loggingLog.setThreadName(Thread.currentThread().getName());
        loggingLog.setLogLevel(event.getLevel().toInt());
        loggingLog.setLogLevelStr(event.getLevel().toString());

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

        // 链路信息
        loggingLog.setTraceId(TraceContext.traceId());

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
