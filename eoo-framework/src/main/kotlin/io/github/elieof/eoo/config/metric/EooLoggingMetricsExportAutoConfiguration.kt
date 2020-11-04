package io.github.elieof.eoo.config.metric

import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.Slf4jReporter
import io.github.elieof.eoo.config.EooProperties
import io.micrometer.core.instrument.Clock
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.dropwizard.DropwizardConfig
import io.micrometer.core.instrument.dropwizard.DropwizardMeterRegistry
import io.micrometer.core.instrument.util.HierarchicalNameMapper
import mu.KotlinLogging
import org.slf4j.LoggerFactory
import org.slf4j.MarkerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

private val logger = KotlinLogging.logger {}


/**
 * Console Reporter Configuration.
 *
 * Pass the metrics to the logs with Dropwizard Reporter implementation
 * see [micrometer](https://github.com/micrometer-metrics/micrometer-docs/blob/9fedeb5/src/docs/guide/console-reporter.adoc)
 */
@Configuration
@ConditionalOnProperty("eoo.metrics.logs.enabled")
class EooLoggingMetricsExportAutoConfiguration(val eooProperties: EooProperties) {

    @Bean
    fun dropwizardRegistry(): MetricRegistry {
        return MetricRegistry()
    }

    @Bean
    fun consoleReporter(dropwizardRegistry: MetricRegistry): Slf4jReporter {
        logger.info("Initialiazing Metrics Log reporting.")
        val metricsMarker = MarkerFactory.getMarker("metrics")
        val reporter = Slf4jReporter.forRegistry(dropwizardRegistry)
            .outputTo(LoggerFactory.getLogger("metrics"))
            .markWith(metricsMarker)
            .convertRatesTo(TimeUnit.SECONDS)
            .build()
        reporter.start(eooProperties.metrics.logs.frequencyReport, TimeUnit.SECONDS)
        return reporter
    }

    @Bean
    fun consoleLoggingRegistry(dropwizardRegistry: MetricRegistry): MeterRegistry {
        val dropwizardConfig = object : DropwizardConfig {
            override fun prefix(): String {
                return "console"
            }

            override fun get(key: String): String? {
                return null
            }
        }

        return object : DropwizardMeterRegistry(
            dropwizardConfig,
            dropwizardRegistry,
            HierarchicalNameMapper.DEFAULT,
            Clock.SYSTEM
        ) {
            override fun nullGaugeValue(): Double {
                return 0.0
            }
        }
    }
}
