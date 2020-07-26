package io.github.elieof.eoo.logging

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.boolex.OnMarkerEvaluator
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.classic.spi.LoggerContextListener
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.filter.EvaluatorFilter
import ch.qos.logback.core.rolling.*
import ch.qos.logback.core.spi.ContextAwareBase
import ch.qos.logback.core.spi.FilterReply
import ch.qos.logback.core.util.FileSize
import io.github.elieof.eoo.EooProperties
import net.logstash.logback.appender.LogstashTcpSocketAppender
import net.logstash.logback.composite.ContextJsonProvider
import net.logstash.logback.composite.GlobalCustomFieldsJsonProvider
import net.logstash.logback.composite.loggingevent.*
import net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder
import net.logstash.logback.encoder.LogstashEncoder
import net.logstash.logback.stacktrace.ShortenedThrowableConverter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.net.InetSocketAddress

/**
 * Utility methods to add appenders to a [ch.qos.logback.classic.LoggerContext].
 */
object LoggingUtils {
    private val log: Logger = LoggerFactory.getLogger(LoggingUtils::class.java)
    private const val FILE_APPENDER_NAME = "FILE"
    private const val ACCESS_FILE_APPENDER_NAME = "ACCESS"
    private const val CONSOLE_APPENDER_NAME = "CONSOLE"
    private const val LOGSTASH_APPENDER_NAME = "LOGSTASH"
    private const val ASYNC_LOGSTASH_APPENDER_NAME = "ASYNC_LOGSTASH"

    /**
     *
     * addJsonConsoleAppender.
     *
     * @param context a [ch.qos.logback.classic.LoggerContext] object.
     * @param customFields a [String] object.
     */
    fun addJsonConsoleAppender(context: LoggerContext, customFields: String) {
        log.info("Initializing Console loggingProperties")

        // More documentation is available at: https://github.com/logstash/logstash-logback-encoder
        val consoleAppender: ConsoleAppender<ILoggingEvent> = ConsoleAppender()
        consoleAppender.context = context
        consoleAppender.encoder = compositeJsonEncoder(context, customFields)

        consoleAppender.name = CONSOLE_APPENDER_NAME
        consoleAppender.start()
        context.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME)
            .detachAppender(CONSOLE_APPENDER_NAME)
        context.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME).addAppender(consoleAppender)
    }

    /**
     *
     * addFileAppender.
     *
     * @param context a [ch.qos.logback.classic.LoggerContext] object.
     */
    fun addFileAppender(
        context: LoggerContext,
        fileProperties: EooProperties.Logging.File
    ) {
        log.info("Initializing File loggingProperties")

        // More documentation is available at: https://github.com/logstash/logstash-logback-encoder
        val fileAppender: RollingFileAppender<ILoggingEvent> = RollingFileAppender()
        fileAppender.context = context
        fileAppender.name = FILE_APPENDER_NAME
        fileAppender.file = fileProperties.dir + File.pathSeparator + fileProperties.prefix + ".log"
        fileAppender.rollingPolicy = fixedWindowRollingPolicy(context, fileProperties)
        fileAppender.triggeringPolicy = triggeringPolicy(context, fileProperties)

        val consoleAppender: ConsoleAppender<ILoggingEvent> =
            context.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME)
                .getAppender(CONSOLE_APPENDER_NAME) as ConsoleAppender<ILoggingEvent>
        fileAppender.encoder = consoleAppender.encoder

        fileAppender.start()
        context.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME).addAppender(fileAppender)
    }

    /**
     *
     * addAccessFileAppender.
     *
     * @param context a [ch.qos.logback.classic.LoggerContext] object.
     */
    fun addAccessFileAppender(
        context: LoggerContext,
        fileProperties: EooProperties.Logging.File
    ) {
        log.info("Initializing Access File loggingProperties")

        // More documentation is available at: https://github.com/logstash/logstash-logback-encoder
        val accessFileAppender: RollingFileAppender<ILoggingEvent> = RollingFileAppender()
        accessFileAppender.context = context
        accessFileAppender.encoder = patternLayoutEncoder()

        accessFileAppender.name = ACCESS_FILE_APPENDER_NAME
        accessFileAppender.file = fileProperties.dir + File.pathSeparator + fileProperties.prefix + ".log"
        accessFileAppender.rollingPolicy = timeBasedRollingPolicy(context, fileProperties)
        accessFileAppender.start()
        context.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME).addAppender(accessFileAppender)
    }

    /**
     *
     * addLogstashTcpSocketAppender.
     *
     * @param context a [ch.qos.logback.classic.LoggerContext] object.
     * @param customFields a [String] object.
     * @param logstashProperties a [io.github.elieof.eoo.EooProperties.Logging.Logstash] object.
     */
    fun addLogstashTcpSocketAppender(
        context: LoggerContext,
        customFields: String,
        logstashProperties: EooProperties.Logging.Logstash
    ) {
        log.info("Initializing Logstash loggingProperties")

        // More documentation is available at: https://github.com/logstash/logstash-logback-encoder
        val logstashAppender = LogstashTcpSocketAppender()
        logstashAppender.addDestinations(InetSocketAddress(logstashProperties.host, logstashProperties.port))
        logstashAppender.context = context
        logstashAppender.encoder = logstashEncoder(customFields)
        logstashAppender.name = ASYNC_LOGSTASH_APPENDER_NAME
        logstashAppender.queueSize = logstashProperties.queueSize
        logstashAppender.start()
        context.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME).addAppender(logstashAppender)
    }

    /**
     *
     * addContextListener.
     *
     * @param context a [ch.qos.logback.classic.LoggerContext] object.
     * @param customFields a [String] object.
     * @param properties a [io.github.elieof.eoo.EooProperties.Logging] object.
     */
    fun addContextListener(
        context: LoggerContext,
        customFields: String,
        properties: EooProperties.Logging
    ) {
        val loggerContextListener = LogbackLoggerContextListener(properties, customFields)
        loggerContextListener.context = context
        context.addListener(loggerContextListener)
    }

    /**
     * Configure a log filter to remove "metrics" logs from all appenders except the "LOGSTASH" appender
     *
     * @param context the logger context
     * @param useJsonFormat whether to use JSON format
     */
    fun setMetricsMarkerLogbackFilter(context: LoggerContext, useJsonFormat: Boolean) {
        log.info("Filtering metrics logs from all appenders except the {} appender", LOGSTASH_APPENDER_NAME)
        val onMarkerMetricsEvaluator = OnMarkerEvaluator()
        onMarkerMetricsEvaluator.context = context
        onMarkerMetricsEvaluator.addMarker("metrics")
        onMarkerMetricsEvaluator.start()
        val metricsFilter: EvaluatorFilter<ILoggingEvent> = EvaluatorFilter()
        metricsFilter.context = context
        metricsFilter.evaluator = onMarkerMetricsEvaluator
        metricsFilter.onMatch = FilterReply.DENY
        metricsFilter.start()
        context.loggerList.forEach { logger ->
            logger.iteratorForAppenders().forEachRemaining { appender ->
                if (appender.name != ASYNC_LOGSTASH_APPENDER_NAME &&
                    !(appender.name == CONSOLE_APPENDER_NAME && useJsonFormat)
                ) {
                    log.debug("Filter metrics logs from the {} appender", appender.name)
                    appender.context = context
                    appender.addFilter(metricsFilter)
                    appender.start()
                }
            }
        }
    }

    private fun patternLayoutEncoder(): PatternLayoutEncoder {
        val encoder = PatternLayoutEncoder()
        encoder.pattern = "%d{yyyy/MM/dd HH:mm:ss,SSS} [%thread] %-5level %logger{36} - %m%n"
        return encoder
    }

    private fun compositeJsonEncoder(
        context: LoggerContext,
        customFields: String
    ): LoggingEventCompositeJsonEncoder {
        val compositeJsonEncoder = LoggingEventCompositeJsonEncoder()
        compositeJsonEncoder.context = context
        compositeJsonEncoder.providers = jsonProviders(context, customFields)
        compositeJsonEncoder.start()
        return compositeJsonEncoder
    }

    private fun logstashEncoder(customFields: String): LogstashEncoder {
        val logstashEncoder = LogstashEncoder()
        logstashEncoder.throwableConverter = throwableConverter()
        logstashEncoder.customFields = customFields
        return logstashEncoder
    }

    private fun jsonProviders(context: LoggerContext, customFields: String): LoggingEventJsonProviders {
        val jsonProviders = LoggingEventJsonProviders()
        jsonProviders.addArguments(ArgumentsJsonProvider())
        jsonProviders.addContext(ContextJsonProvider())
        jsonProviders.addGlobalCustomFields(customFieldsJsonProvider(customFields))
        jsonProviders.addLogLevel(LogLevelJsonProvider())
        jsonProviders.addLoggerName(loggerNameJsonProvider())
        jsonProviders.addMdc(MdcJsonProvider())
        jsonProviders.addMessage(MessageJsonProvider())
        jsonProviders.addPattern(LoggingEventPatternJsonProvider())
        jsonProviders.addStackTrace(stackTraceJsonProvider())
        jsonProviders.addThreadName(ThreadNameJsonProvider())
        jsonProviders.addTimestamp(timestampJsonProvider())
        jsonProviders.setContext(context)
        return jsonProviders
    }

    private fun customFieldsJsonProvider(customFields: String): GlobalCustomFieldsJsonProvider<ILoggingEvent> {
        val customFieldsJsonProvider: GlobalCustomFieldsJsonProvider<ILoggingEvent> = GlobalCustomFieldsJsonProvider()
        customFieldsJsonProvider.customFields = customFields
        return customFieldsJsonProvider
    }

    private fun loggerNameJsonProvider(): LoggerNameJsonProvider {
        val loggerNameJsonProvider = LoggerNameJsonProvider()
        loggerNameJsonProvider.shortenedLoggerNameLength = 25
        return loggerNameJsonProvider
    }

    private fun stackTraceJsonProvider(): StackTraceJsonProvider {
        val stackTraceJsonProvider = StackTraceJsonProvider()
        stackTraceJsonProvider.throwableConverter = throwableConverter()
        return stackTraceJsonProvider
    }

    private fun throwableConverter(): ShortenedThrowableConverter {
        val throwableConverter = ShortenedThrowableConverter()
        throwableConverter.isRootCauseFirst = true
        return throwableConverter
    }

    private fun timestampJsonProvider(): LoggingEventFormattedTimestampJsonProvider {
        val timestampJsonProvider = LoggingEventFormattedTimestampJsonProvider()
        timestampJsonProvider.timeZone = "UTC"
        timestampJsonProvider.fieldName = "timestamp"
        return timestampJsonProvider
    }

    private fun fixedWindowRollingPolicy(
        context: LoggerContext,
        fileProperties: EooProperties.Logging.File
    ): RollingPolicy {
        val policy = FixedWindowRollingPolicy()
        policy.minIndex = fileProperties.minIndex
        policy.maxIndex = fileProperties.maxIndex
        policy.context = context
        policy.fileNamePattern = fileProperties.dir + "/archive/${fileProperties.prefix}.%i.log.gz"
        return policy
    }

    private fun timeBasedRollingPolicy(
        context: LoggerContext,
        fileProperties: EooProperties.Logging.File
    ): RollingPolicy {
        val policy: TimeBasedRollingPolicy<ILoggingEvent> = TimeBasedRollingPolicy()
        policy.context = context
        policy.fileNamePattern = fileProperties.dir + "/archive/${fileProperties.prefix}.%d{yyyy-MM-dd}.log.gz"
        policy.maxHistory = fileProperties.maxHistory
        return policy
    }

    private fun triggeringPolicy(
        context: LoggerContext,
        fileProperties: EooProperties.Logging.File
    ): TriggeringPolicyBase<ILoggingEvent> {
        val policy: SizeBasedTriggeringPolicy<ILoggingEvent> = SizeBasedTriggeringPolicy()
        policy.context = context
        policy.setMaxFileSize(FileSize.valueOf(fileProperties.maxSize))
        return policy
    }

    /**
     * Logback configuration is achieved by configuration file and API.
     * When configuration file change is detected, the configuration is reset.
     * This listener ensures that the programmatic configuration is also re-applied after reset.
     */
    private class LogbackLoggerContextListener internal constructor(
        val loggingProperties: EooProperties.Logging,
        val customFields: String
    ) : ContextAwareBase(), LoggerContextListener {

        override fun isResetResistant(): Boolean {
            return true
        }

        override fun onStart(context: LoggerContext) {
            initContext(context)
        }

        override fun onReset(context: LoggerContext) {
            initContext(context)
        }

        override fun onStop(context: LoggerContext?) {
            // Nothing to do.
        }

        override fun onLevelChange(logger: ch.qos.logback.classic.Logger?, level: Level?) {
            // Nothing to do.
        }

        private fun initContext(context: LoggerContext) {
            if (loggingProperties.useJsonFormat) {
                addJsonConsoleAppender(context, customFields)
            }
            if (loggingProperties.file.enabled) {
                addFileAppender(context, loggingProperties.file)
            }
            if (loggingProperties.accessFile.enabled) {
                addAccessFileAppender(context, loggingProperties.accessFile)
            }
            if (loggingProperties.logstash.enabled) {
                addLogstashTcpSocketAppender(context, customFields, loggingProperties.logstash)
            }
        }
    }
}