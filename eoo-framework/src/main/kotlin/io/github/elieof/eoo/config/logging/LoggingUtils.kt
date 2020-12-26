@file:Suppress("TooManyFunctions")

package io.github.elieof.eoo.config.logging

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.classic.spi.LoggerContextListener
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.rolling.FixedWindowRollingPolicy
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.RollingPolicy
import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy
import ch.qos.logback.core.rolling.TriggeringPolicyBase
import ch.qos.logback.core.spi.ContextAwareBase
import ch.qos.logback.core.util.FileSize
import io.github.elieof.eoo.config.EooProperties
import mu.KotlinLogging
import net.logstash.logback.appender.LogstashTcpSocketAppender
import net.logstash.logback.composite.ContextJsonProvider
import net.logstash.logback.composite.GlobalCustomFieldsJsonProvider
import net.logstash.logback.composite.loggingevent.ArgumentsJsonProvider
import net.logstash.logback.composite.loggingevent.LogLevelJsonProvider
import net.logstash.logback.composite.loggingevent.LoggerNameJsonProvider
import net.logstash.logback.composite.loggingevent.LoggingEventFormattedTimestampJsonProvider
import net.logstash.logback.composite.loggingevent.LoggingEventJsonProviders
import net.logstash.logback.composite.loggingevent.LoggingEventPatternJsonProvider
import net.logstash.logback.composite.loggingevent.MdcJsonProvider
import net.logstash.logback.composite.loggingevent.MessageJsonProvider
import net.logstash.logback.composite.loggingevent.StackTraceJsonProvider
import net.logstash.logback.composite.loggingevent.ThreadNameJsonProvider
import net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder
import net.logstash.logback.encoder.LogstashEncoder
import net.logstash.logback.stacktrace.ShortenedThrowableConverter
import java.net.InetSocketAddress

/**
 * Utility methods to add appenders to a [ch.qos.logback.classic.LoggerContext].
 */
object LoggingUtils {
    private val logger = KotlinLogging.logger {}
    internal const val FILE_APPENDER_NAME = "FILE"
    internal const val ACCESS_FILE_APPENDER_NAME = "ACCESS"
    internal const val DEFAULT_CONSOLE_APPENDER_NAME = "console"
    internal const val CONSOLE_APPENDER_NAME = "CONSOLE"
    internal const val ASYNC_LOGSTASH_APPENDER_NAME = "ASYNC_LOGSTASH"
    internal const val ACCESS_FILE_PATTERN = "%d{yyyy/MM/dd HH:mm:ss,SSS} [%thread] %-5level %logger{36} - %m%n"
    internal const val LOGGER_LOGBOOK = "org.zalando.logbook"
    private const val LOGGER_NAME_LENGTH = 25

    /**
     *
     * addJsonConsoleAppender.
     *
     * @param context a [ch.qos.logback.classic.LoggerContext] object.
     * @param customFields a [String] object.
     */
    fun addJsonConsoleAppender(context: LoggerContext, customFields: String) {
        logger.info("Initializing Console loggingProperties")

        // More documentation is available at: https://github.com/logstash/logstash-logback-encoder
        val consoleAppender: ConsoleAppender<ILoggingEvent> = ConsoleAppender()
        consoleAppender.context = context
        consoleAppender.encoder = compositeJsonEncoder(context, customFields)

        consoleAppender.name = CONSOLE_APPENDER_NAME
        consoleAppender.start()
        context.getLogger(ROOT_LOGGER_NAME).detachAppender(CONSOLE_APPENDER_NAME)
        context.getLogger(ROOT_LOGGER_NAME).addAppender(consoleAppender)
    }

    /**
     *
     * addFileAppender.
     *
     * @param context a [ch.qos.logback.classic.LoggerContext] object.
     */
    fun addFileAppender(
        context: LoggerContext,
        fileProperties: EooProperties.Logging.AppFile
    ) {
        logger.info("Initializing File loggingProperties")

        // More documentation is available at: https://github.com/logstash/logstash-logback-encoder
        val fileAppender: RollingFileAppender<ILoggingEvent> = RollingFileAppender()
        fileAppender.context = context
        fileAppender.name = FILE_APPENDER_NAME
        fileAppender.file = getLogFileName(fileProperties)
        fileAppender.rollingPolicy = fixedWindowRollingPolicy(context, fileProperties)

        fileAppender.rollingPolicy.setParent(fileAppender)
        fileAppender.rollingPolicy.start()
        fileAppender.triggeringPolicy = triggeringPolicy(context, fileProperties)
        fileAppender.triggeringPolicy.start()

        val consoleAppender: ConsoleAppender<ILoggingEvent> =
            (
                context.getLogger(ROOT_LOGGER_NAME).getAppender(CONSOLE_APPENDER_NAME) ?: context.getLogger(
                    ROOT_LOGGER_NAME
                ).getAppender(DEFAULT_CONSOLE_APPENDER_NAME)
                ) as ConsoleAppender<ILoggingEvent>
        fileAppender.encoder = consoleAppender.encoder

        fileAppender.start()
        context.getLogger(ROOT_LOGGER_NAME).detachAppender(FILE_APPENDER_NAME)
        context.getLogger(ROOT_LOGGER_NAME).addAppender(fileAppender)
    }

    /**
     *
     * addAccessFileAppender.
     *
     * @param context a [ch.qos.logback.classic.LoggerContext] object.
     */
    fun addAccessFileAppender(
        context: LoggerContext,
        fileProperties: EooProperties.Logging.AccessFile
    ) {
        logger.info("Initializing Access File loggingProperties")

        // More documentation is available at: https://github.com/logstash/logstash-logback-encoder
        val accessFileAppender: RollingFileAppender<ILoggingEvent> = RollingFileAppender()
        accessFileAppender.context = context
        accessFileAppender.encoder = patternLayoutEncoder(context)

        accessFileAppender.name = ACCESS_FILE_APPENDER_NAME
        accessFileAppender.file = getLogFileName(fileProperties)
        accessFileAppender.rollingPolicy = timeBasedRollingPolicy(context, fileProperties)
        accessFileAppender.rollingPolicy.setParent(accessFileAppender)
        accessFileAppender.rollingPolicy.start()
        accessFileAppender.start()
        context.getLogger(LOGGER_LOGBOOK).detachAppender(ACCESS_FILE_APPENDER_NAME)
        context.getLogger(LOGGER_LOGBOOK).addAppender(accessFileAppender)
        context.getLogger(LOGGER_LOGBOOK).isAdditive = false
        context.getLogger(LOGGER_LOGBOOK).level = Level.TRACE
    }

    /**
     *
     * addLogstashTcpSocketAppender.
     *
     * @param context a [ch.qos.logback.classic.LoggerContext] object.
     * @param customFields a [String] object.
     * @param logstashProperties a [io.github.elieof.eoo.config.EooProperties.Logging.Logstash] object.
     */
    fun addLogstashTcpSocketAppender(
        context: LoggerContext,
        customFields: String,
        logstashProperties: EooProperties.Logging.Logstash
    ) {
        logger.info("Initializing Logstash loggingProperties")

        // More documentation is available at: https://github.com/logstash/logstash-logback-encoder
        val logstashAppender = LogstashTcpSocketAppender()
        logstashAppender.addDestinations(InetSocketAddress(logstashProperties.host, logstashProperties.port))
        logstashAppender.context = context
        logstashAppender.encoder = logstashEncoder(customFields)
        logstashAppender.name = ASYNC_LOGSTASH_APPENDER_NAME
        logstashAppender.queueSize = logstashProperties.queueSize
        logstashAppender.start()
        context.getLogger(ROOT_LOGGER_NAME).detachAppender(ASYNC_LOGSTASH_APPENDER_NAME)
        context.getLogger(ROOT_LOGGER_NAME).addAppender(logstashAppender)

        context.getLogger(LOGGER_LOGBOOK).detachAppender(ASYNC_LOGSTASH_APPENDER_NAME)
        context.getLogger(LOGGER_LOGBOOK).addAppender(logstashAppender)
        context.getLogger(LOGGER_LOGBOOK).isAdditive = false
        context.getLogger(LOGGER_LOGBOOK).level = Level.TRACE
    }

    /**
     *
     * addContextListener.
     *
     * @param context a [ch.qos.logback.classic.LoggerContext] object.
     * @param customFields a [String] object.
     * @param properties a [io.github.elieof.eoo.config.EooProperties.Logging] object.
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

    private fun getLogFileName(fileProperties: EooProperties.Logging.File): String {
        val dir = if (fileProperties.dir.isEmpty())
            System.getProperty("java.io.tmpdir") + "/.log" else fileProperties.dir
        return dir + "/" + fileProperties.prefix + ".log"
    }

    private fun patternLayoutEncoder(context: LoggerContext): PatternLayoutEncoder {
        val encoder = PatternLayoutEncoder()
        encoder.pattern = ACCESS_FILE_PATTERN
        encoder.context = context
        encoder.start()
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
        loggerNameJsonProvider.shortenedLoggerNameLength = LOGGER_NAME_LENGTH
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
        fileProperties: EooProperties.Logging.AppFile
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
        fileProperties: EooProperties.Logging.AccessFile
    ): RollingPolicy {
        val policy: TimeBasedRollingPolicy<ILoggingEvent> = TimeBasedRollingPolicy()
        policy.context = context
        policy.fileNamePattern = fileProperties.dir + "/archive/${fileProperties.prefix}.%d{yyyy-MM-dd}.log.gz"
        policy.maxHistory = fileProperties.maxHistory
        return policy
    }

    private fun triggeringPolicy(
        context: LoggerContext,
        fileProperties: EooProperties.Logging.AppFile
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
    internal class LogbackLoggerContextListener(
        private val loggingProperties: EooProperties.Logging,
        private val customFields: String
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
