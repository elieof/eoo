package io.github.elieof.eoo.config.logging

import ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.encoder.LayoutWrappingEncoder
import ch.qos.logback.core.rolling.FixedWindowRollingPolicy
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy
import ch.qos.logback.core.util.FileSize
import io.github.elieof.eoo.config.EooDefaults
import io.github.elieof.eoo.config.EooProperties
import io.github.elieof.eoo.config.logging.LoggingUtils.ACCESS_FILE_APPENDER_NAME
import io.github.elieof.eoo.config.logging.LoggingUtils.ASYNC_LOGSTASH_APPENDER_NAME
import io.github.elieof.eoo.config.logging.LoggingUtils.CONSOLE_APPENDER_NAME
import io.github.elieof.eoo.config.logging.LoggingUtils.FILE_APPENDER_NAME
import io.github.elieof.eoo.config.logging.LoggingUtils.LOGGER_LOGBOOK
import net.logstash.logback.appender.LogstashTcpSocketAppender
import net.logstash.logback.composite.loggingevent.LoggingEventJsonProviders
import net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.InstanceOfAssertFactories.type
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.net.InetSocketAddress

internal class LoggingUtilsTest {

    private lateinit var loggingProperties: EooProperties.Logging

    private lateinit var context: LoggerContext

    @BeforeEach
    internal fun setUp() {
        context = LoggerContext()
        loggingProperties = EooProperties.Logging(
            file = EooProperties.Logging.AppFile(dir = "./out"),
            accessFile = EooProperties.Logging.AccessFile(dir = "./out")
        )
    }

    @Test
    fun addJsonConsoleAppender() {
        LoggingUtils.addJsonConsoleAppender(context, "")
        val logger = context.getLogger(ROOT_LOGGER_NAME)
        assertThat(logger).isNotNull
        assertThat(logger.getAppender(CONSOLE_APPENDER_NAME)).isNotNull
            .asInstanceOf(type(ConsoleAppender::class.java))
            .extracting { it.encoder }
            .asInstanceOf(type(LoggingEventCompositeJsonEncoder::class.java))
            .extracting { it.providers }
            .asInstanceOf(type(LoggingEventJsonProviders::class.java))

        logger.detachAppender(CONSOLE_APPENDER_NAME)
    }

    @Test
    fun addFileAppender() {
        context = org.slf4j.LoggerFactory.getILoggerFactory() as LoggerContext
        val fileProperties = loggingProperties.file
        LoggingUtils.addFileAppender(context, fileProperties)

        val logger = context.getLogger(ROOT_LOGGER_NAME)
        assertThat(logger).isNotNull

        val appender = logger.getAppender(FILE_APPENDER_NAME)
        assertThat(appender).isNotNull
            .asInstanceOf(type(RollingFileAppender::class.java))
            .hasFieldOrPropertyWithValue(
                "fileName",
                fileProperties.dir + "/" + fileProperties.prefix + ".log"
            )
            .extracting { it.encoder }
            .isInstanceOf(LayoutWrappingEncoder::class.java)

        assertThat(appender)
            .asInstanceOf(type(RollingFileAppender::class.java))
            .extracting { it.rollingPolicy }
            .asInstanceOf(type(FixedWindowRollingPolicy::class.java))
            .hasFieldOrPropertyWithValue("minIndex", fileProperties.minIndex)
            .hasFieldOrPropertyWithValue("maxIndex", fileProperties.maxIndex)
            .hasFieldOrPropertyWithValue(
                "fileNamePattern",
                fileProperties.dir + "/archive/${fileProperties.prefix}.%i.log.gz"
            )

        assertThat(appender)
            .asInstanceOf(type(RollingFileAppender::class.java))
            .extracting { it.triggeringPolicy }
            .asInstanceOf(type(SizeBasedTriggeringPolicy::class.java))
            .hasFieldOrPropertyWithValue("maxFileSize.size", FileSize.valueOf(fileProperties.maxSize).size)

        logger.detachAppender(FILE_APPENDER_NAME)
    }

    @Test
    fun addFileAppenderOnJsonConsoleAppender() {
        LoggingUtils.addJsonConsoleAppender(context, "")
        LoggingUtils.addFileAppender(context, loggingProperties.file)
        val logger = context.getLogger(ROOT_LOGGER_NAME)
        assertThat(logger).isNotNull

        assertThat(logger.getAppender(FILE_APPENDER_NAME)).isNotNull
            .asInstanceOf(type(RollingFileAppender::class.java))
            .extracting { it.encoder }
            .isInstanceOf(LoggingEventCompositeJsonEncoder::class.java)

        logger.detachAppender(CONSOLE_APPENDER_NAME)
        logger.detachAppender(FILE_APPENDER_NAME)
    }

    @Test
    fun addAccessFileAppender() {
        val fileProperties = loggingProperties.accessFile
        LoggingUtils.addAccessFileAppender(context, fileProperties)
        val logger = context.getLogger(LOGGER_LOGBOOK)
        assertThat(logger).isNotNull
        assertThat(logger.getAppender(ACCESS_FILE_APPENDER_NAME)).isNotNull
            .asInstanceOf(type(RollingFileAppender::class.java))
            .hasFieldOrPropertyWithValue(
                "fileName",
                fileProperties.dir + "/" + EooDefaults.Logging.File.accessPrefix + ".log"
            )
            .hasFieldOrPropertyWithValue("encoder.pattern", LoggingUtils.ACCESS_FILE_PATTERN)
            .extracting { it.encoder }
            .isInstanceOf(PatternLayoutEncoder::class.java)

        assertThat(logger.getAppender(ACCESS_FILE_APPENDER_NAME))
            .asInstanceOf(type(RollingFileAppender::class.java))
            .extracting { it.rollingPolicy }
            .asInstanceOf(type(TimeBasedRollingPolicy::class.java))
            .hasFieldOrPropertyWithValue("maxHistory", fileProperties.maxHistory)
            .hasFieldOrPropertyWithValue(
                "fileNamePattern",
                fileProperties.dir + "/archive/${fileProperties.prefix}.%d{yyyy-MM-dd}.log.gz"
            )
        logger.detachAppender(ACCESS_FILE_APPENDER_NAME)
    }

    @Test
    fun addLogstashTcpSocketAppender() {
        val logstashProperties = loggingProperties.logstash
        LoggingUtils.addLogstashTcpSocketAppender(context, "{\"customFields\": true}", logstashProperties)
        val logger = context.getLogger(ROOT_LOGGER_NAME)
        assertThat(logger).isNotNull
        assertThat(logger.getAppender(ASYNC_LOGSTASH_APPENDER_NAME)).isNotNull
            .asInstanceOf(type(LogstashTcpSocketAppender::class.java))
            .hasFieldOrPropertyWithValue("queueSize", logstashProperties.queueSize)
            .hasFieldOrPropertyWithValue("encoder.customFields", "{\"customFields\": true}")
            .extracting { it.destinations[0] }
            .isInstanceOf(InetSocketAddress::class.java)
        val address = (logger.getAppender(ASYNC_LOGSTASH_APPENDER_NAME) as LogstashTcpSocketAppender)
            .destinations.toString()
        assertThat(address).contains(logstashProperties.host)
        assertThat(address).contains(logstashProperties.port.toString())
        logger.detachAppender(ASYNC_LOGSTASH_APPENDER_NAME)
    }

    @Test
    fun addContextListener() {

        loggingProperties = EooProperties.Logging(
            useJsonFormat = true,
            file = EooProperties.Logging.AppFile(enabled = true, dir = "./out"),
            accessFile = EooProperties.Logging.AccessFile(enabled = true, dir = "./out"),
            logstash = EooProperties.Logging.Logstash(true)
        )
        LoggingUtils.addContextListener(context, "{\"customFields\": true}", loggingProperties)
        assertThat(context.copyOfListenerList).hasSizeGreaterThanOrEqualTo(1)
        val loggerContextListener = context.copyOfListenerList[0]
        assertThat(loggerContextListener).isInstanceOf(LoggingUtils.LogbackLoggerContextListener::class.java)
        assertThat(context.copyOfListenerList).extracting("customFields").contains("{\"customFields\": true}")
        assertThat(context.copyOfListenerList).extracting("loggingProperties").contains(loggingProperties)

        loggerContextListener.onStart(context)

        val logBookLogger = context.getLogger(LOGGER_LOGBOOK)
        assertThat(logBookLogger).isNotNull
        assertThat(logBookLogger.getAppender(ACCESS_FILE_APPENDER_NAME)).isNotNull.isInstanceOf(
            RollingFileAppender::class.java
        )

        val logger = context.getLogger(ROOT_LOGGER_NAME)
        assertThat(logger).isNotNull
        assertThat(logger.getAppender(CONSOLE_APPENDER_NAME)).isNotNull.isInstanceOf(ConsoleAppender::class.java)
        assertThat(logger.getAppender(FILE_APPENDER_NAME)).isNotNull.isInstanceOf(RollingFileAppender::class.java)
        assertThat(logger.getAppender(ASYNC_LOGSTASH_APPENDER_NAME)).isNotNull.isInstanceOf(
            LogstashTcpSocketAppender::class.java
        )

        logger.detachAppender(CONSOLE_APPENDER_NAME)
        logger.detachAppender(FILE_APPENDER_NAME)
        logger.detachAppender(ACCESS_FILE_APPENDER_NAME)
        logger.detachAppender(ASYNC_LOGSTASH_APPENDER_NAME)
        context.removeListener(loggerContextListener)
    }
}
