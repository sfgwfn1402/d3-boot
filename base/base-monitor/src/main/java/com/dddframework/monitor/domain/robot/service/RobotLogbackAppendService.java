package com.dddframework.monitor.domain.robot.service;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.boolex.JaninoEventEvaluator;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.EvaluatorFilter;
import ch.qos.logback.core.spi.FilterReply;
import com.dddframework.kit.lang.StrKit;
import com.dddframework.monitor.domain.robot.model.RobotAppender;
import com.dddframework.monitor.infras.config.BaseMonitorProperties;
import com.dddframework.monitor.infras.utils.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

import static ch.qos.logback.core.AsyncAppenderBase.DEFAULT_MAX_FLUSH_TIME;

/**
 * @author Jensen
 * @公众号 架构师修行录
 */
@Slf4j(topic = "### BASE-MONITOR : RobotLogbackAppendService ###")
public class RobotLogbackAppendService {
    /**
     * logback loggerContext
     */
    private LoggerContext loggerContext = null;
    @Autowired
    private BaseMonitorProperties baseMonitorProperties;

    @PostConstruct
    public void init() {
        this.initLoggerContext();
        loggerContext.putProperty("ip", IpUtils.getLocalAddress());
        if (baseMonitorProperties.getLog().getApp() != null && StrKit.isNotBlank(baseMonitorProperties.getLog().getApp().getProject())) {
            loggerContext.putProperty("project", baseMonitorProperties.getLog().getApp().getProject());
        }
        AsyncAppender asyncAppender = this.asyncAppender();
        asyncAppender.start();
        Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.addAppender(asyncAppender);
    }

    @Bean(destroyMethod = "stop")
    public RobotAppender robotAppender() {
        return RobotAppender.build(loggerContext);
    }

    /**
     * 构建 异步的 AsyncAppender 包装RobotAppend
     */
    @Bean(destroyMethod = "stop")
    public AsyncAppender asyncAppender() {
        AsyncAppender asyncAppender = new AsyncAppender();
        BaseMonitorProperties.Log.Config config = baseMonitorProperties.getLog().getConfig();
        asyncAppender.setContext(loggerContext);
        // http://logback.qos.ch/manual/appenders.html#AsyncAppender
        // 提取调用方数据可能相当昂贵。
        // 若要提高性能，默认情况下，当事件添加到事件队列时，不会提取与事件关联的调用方数据。
        // 默认情况下，只有“廉价”数据，如线程名和 都被复制了。
        asyncAppender.setIncludeCallerData(config.getAsyncAppenderIncludeCallerData());

        //blockingQueue长度决定了队列能放多少信息，在默认的配置下，如果blockingQueue放满了，后续想要输出日志的线程会被阻塞，直到Worker线程处理掉队列中的信息为止。
        // 根据实际情况适当调整队列长度，可以防止线程被阻塞。
        asyncAppender.setQueueSize(config.getAsyncAppenderQueueSize());

        // 默认情况下，当阻塞队列剩余20% 的容量时，它将删除 TRACE、 DEBUG 和 INFO 级别的事件，只保留 WARN 和 ERROR 级别的事件。
        // super.setDiscardingThreshold();

        //如果配置neverBlock=true，当队列满了之后，后面阻塞的线程想要输出的消息就直接被丢弃，从而线程不会阻塞。
        // 这个配置用于线程很重要，不能卡顿，而且日志又不是很重要的场景，因为很有可能会丢日志
        asyncAppender.setNeverBlock(config.getAsyncAppenderNeverBlock());

        //Depending on the queue depth and latency to the referenced appender,
        // the AsyncAppender may take an unacceptable amount of time to fully flush the queue.
        // When the LoggerContext is stopped,
        // the AsyncAppender stop method waits up to this timeout for the worker thread to complete.
        // Use maxFlushTime to specify a maximum queue flush timeout in milliseconds.
        // Events that cannot be processed within this window are discarded.
        // Semantics of this value are identical to that of Thread.join(long).
        asyncAppender.setMaxFlushTime(DEFAULT_MAX_FLUSH_TIME);

        asyncAppender.setName("robotAsyncAppender");

        RobotAppender robotAppender = this.robotAppender();
        asyncAppender.addAppender(robotAppender);
        ThresholdFilter thresholdFilter = this.buildThresholdFilter();
        asyncAppender.addFilter(thresholdFilter);
        EvaluatorFilter<ILoggingEvent> ignoresFilter = this.ignores();
        if (ignoresFilter != null) {
            asyncAppender.addFilter(ignoresFilter);
        }
        EvaluatorFilter<ILoggingEvent> ignoreLogNamesFilter = this.ignoreLogNames();
        if (ignoreLogNamesFilter != null) {
            asyncAppender.addFilter(ignoreLogNamesFilter);
        }
        EvaluatorFilter<ILoggingEvent> includesFilter = this.includes();
        if (includesFilter != null) {
            asyncAppender.addFilter(includesFilter);
        }
        EvaluatorFilter<ILoggingEvent> keywordExpressionFilter = this.keywordExpression();
        if (keywordExpressionFilter != null) {
            asyncAppender.addFilter(keywordExpressionFilter);
        }
        this.addLoggerNameRobotAppender(asyncAppender);
        return asyncAppender;
    }

    /**
     * 添加 logger name 到 robot append
     */
    private void addLoggerNameRobotAppender(AsyncAppender asyncAppender) {
        BaseMonitorProperties.Log.Config config = baseMonitorProperties.getLog().getConfig();
        for (String loggerName : config.getAppendLoggerNames()) {
            Logger logger = loggerContext.getLogger(loggerName);
            if (logger == null) {
                log.warn("alarm logger name ={} not found", loggerName);
                continue;
            }
            logger.addAppender(asyncAppender);
        }
    }

    /**
     * 初始化日志上下文 @see org.springframework.boot.logging.logback.LogbackLoggingSystemLogbackLoggingSystem#getLoggerContext()
     */
    private void initLoggerContext() {
        ILoggerFactory factory = LoggerFactory.getILoggerFactory();
        if (!(factory instanceof LoggerContext)) {
            throw new IllegalArgumentException("LoggerFactory is not a Logback LoggerContext");
        }
        this.loggerContext = (LoggerContext) factory;
    }

    /**
     * 构建表达式 过滤器
     */
    private EvaluatorFilter<ILoggingEvent> keywordExpression() {
        BaseMonitorProperties.Log.Config config = baseMonitorProperties.getLog().getConfig();
        if (StringUtils.hasText(config.getKeywordExpression())) {
            return getEvaluatorFilter(config.getKeywordExpression(), FilterReply.ACCEPT, FilterReply.DENY);
        }
        return null;
    }

    /**
     * 包含关键字
     */
    private EvaluatorFilter<ILoggingEvent> includes() {
        BaseMonitorProperties.Log.Config config = baseMonitorProperties.getLog().getConfig();
        if (!CollectionUtils.isEmpty(config.getIncludes())) {
            StringBuilder builder = new StringBuilder("return ");
            for (int index = 0; index < config.getIncludes().size(); index++) {
                String keyword = config.getIncludes().get(index);
                if (index != 0 && index != config.getIncludes().size()) {
                    builder.append(" || ");
                }
                builder.append(" formattedMessage.contains(\"").append(keyword).append("\")");

            }
            builder.append(";");
            return getEvaluatorFilter(builder.toString(), FilterReply.ACCEPT, FilterReply.DENY);
        }
        return null;
    }

    /**
     * 排除 关键 loggerName 的告警
     */
    private EvaluatorFilter<ILoggingEvent> ignoreLogNames() {
        BaseMonitorProperties.Log.Config config = baseMonitorProperties.getLog().getConfig();
        if (!CollectionUtils.isEmpty(config.getIgnoreLogNames())) {
            StringBuilder builder = new StringBuilder("return ");
            for (int index = 0; index < config.getIgnoreLogNames().size(); index++) {
                String keyword = config.getIgnoreLogNames().get(index);
                if (index != 0 && index != config.getIgnoreLogNames().size()) {
                    builder.append(" || ");
                }
                builder.append(" logger.contains(\"").append(keyword).append("\")");
            }
            builder.append(";");
            return getEvaluatorFilter(builder.toString(), FilterReply.DENY, FilterReply.NEUTRAL);
        }
        return null;
    }


    /**
     * 排除 关键 信息的告警
     */
    private EvaluatorFilter<ILoggingEvent> ignores() {
        BaseMonitorProperties.Log.Config config = baseMonitorProperties.getLog().getConfig();
        if (!CollectionUtils.isEmpty(config.getIgnores())) {
            StringBuilder builder = new StringBuilder("return ");
            for (int index = 0; index < config.getIgnores().size(); index++) {
                String keyword = config.getIgnores().get(index);
                if (index != 0 && index != config.getIgnores().size()) {
                    builder.append(" || ");
                }
                builder.append(" formattedMessage.contains(\"").append(keyword).append("\")");
            }
            builder.append(";");
            return getEvaluatorFilter(builder.toString(), FilterReply.DENY, FilterReply.NEUTRAL);
        }
        return null;
    }


    /**
     * 构建表达式 DEBUG INFO WARN ERROR event message formattedMessage logger loggerContext level
     * timeStamp  marker mdc throwableProxy throwable 等等
     *
     * @see <a href="http://logback.qos.ch/manual/filters.html#EvaluatorFilter">EvaluatorFilter</a>
     */
    private EvaluatorFilter<ILoggingEvent> getEvaluatorFilter(String expression, FilterReply onMatch, FilterReply onMismatch) {
        // 可以使用 event、message、logger、loggerContext、mdc、throwable、throwableProxy 等关键字
        EvaluatorFilter<ILoggingEvent> evaluatorFilter = new EvaluatorFilter<>();
        JaninoEventEvaluator eventEvaluator = new JaninoEventEvaluator();
        // 需要存在关键字才打印
        eventEvaluator.setExpression(expression);
        evaluatorFilter.setEvaluator(eventEvaluator);
        eventEvaluator.setContext(loggerContext);
        evaluatorFilter.setOnMatch(onMatch);
        evaluatorFilter.setOnMismatch(onMismatch);
        eventEvaluator.start();
        evaluatorFilter.start();
        return evaluatorFilter;
    }

    /**
     * 构建拦截器 伐值以上的日志都会打印
     *
     * @see <a href="http://logback.qos.ch/manual/filters.html#ThresholdFilter">logback</a>
     */
    private ThresholdFilter buildThresholdFilter() {
        BaseMonitorProperties.Log.Config config = baseMonitorProperties.getLog().getConfig();
        ThresholdFilter thresholdFilter = new ThresholdFilter();
        thresholdFilter.setLevel(config.getLogLevel().name());
        thresholdFilter.start();
        return thresholdFilter;
    }


}
