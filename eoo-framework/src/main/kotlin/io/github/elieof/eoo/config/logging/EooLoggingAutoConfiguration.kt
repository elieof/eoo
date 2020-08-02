package io.github.elieof.eoo.config.logging

import ch.qos.logback.classic.LoggerContext
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.elieof.eoo.config.EooProperties
import io.github.elieof.eoo.config.logging.LoggingUtils.addContextListener
import io.github.elieof.eoo.config.logging.LoggingUtils.setMetricsMarkerLogbackFilter
import net.logstash.logback.encoder.LogstashEncoder
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.info.BuildProperties
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.context.annotation.Configuration

@Configuration
@AutoConfigureAfter(EooProperties::class)
@ConditionalOnClass(LogstashEncoder::class)
@ConditionalOnBean(ObjectMapper::class)
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

        addContextListener(context, customFields, loggingProperties)
        if (eooProperties.metrics.logs.enabled) {
            setMetricsMarkerLogbackFilter(context, loggingProperties.useJsonFormat)
        }
    }
}
