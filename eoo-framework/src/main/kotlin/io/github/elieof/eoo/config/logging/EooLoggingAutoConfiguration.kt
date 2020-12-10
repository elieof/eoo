package io.github.elieof.eoo.config.logging

import ch.qos.logback.classic.LoggerContext
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.elieof.eoo.config.EooProperties
import io.github.elieof.eoo.config.logging.LoggingUtils.addAccessFileAppender
import io.github.elieof.eoo.config.logging.LoggingUtils.addFileAppender
import io.github.elieof.eoo.config.logging.LoggingUtils.addJsonConsoleAppender
import io.github.elieof.eoo.config.logging.LoggingUtils.addLogstashTcpSocketAppender
import net.logstash.logback.encoder.LogstashEncoder
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.boot.info.BuildProperties
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.zalando.logbook.HttpLogFormatter
import org.zalando.logbook.Sink
import org.zalando.logbook.logstash.LogstashLogbackSink

/**
 * Auto configuration class for logging using logback/logstash depending on [EooProperties.Logging]
 */
@Configuration
@ConditionalOnClass(LogstashEncoder::class)
@AutoConfigureAfter(JacksonAutoConfiguration::class)
@ConditionalOnBean(value = [ObjectMapper::class, EooProperties::class])
@RefreshScope
class EooLoggingAutoConfiguration(
    @Value("\${spring.application.name}") appName: String,
    @Value("\${server.port}") serverPort: String,
    eooProperties: EooProperties,
    buildProperties: BuildProperties?,
    mapper: ObjectMapper
) {
    init {
        val context = LoggerFactory.getILoggerFactory() as LoggerContext

        val map = mutableMapOf<String, String?>()
        map["app_name"] = appName
        map["app_port"] = serverPort
        buildProperties?.apply { map["version"] = this.version }
        val customFields = mapper.writeValueAsString(map)

        val loggingProperties = eooProperties.logging

//        addContextListener(context, customFields, loggingProperties)

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

    @Bean
    @ConditionalOnProperty(prefix = "eoo.logging.logstash", name = ["enabled"], havingValue = "true")
    fun sink(formatter: HttpLogFormatter): Sink {
        return LogstashLogbackSink(formatter)
    }
}
