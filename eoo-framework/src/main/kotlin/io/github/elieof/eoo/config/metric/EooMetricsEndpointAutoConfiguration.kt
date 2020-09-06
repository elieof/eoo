package io.github.elieof.eoo.config.metric

import io.micrometer.core.annotation.Timed
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsEndpointAutoConfiguration
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Auto configuration class for metrics using [MetricsEndpointAutoConfiguration]
 */
@Configuration
@ConditionalOnClass(Timed::class)
@AutoConfigureAfter(MetricsEndpointAutoConfiguration::class)
class EooMetricsEndpointAutoConfiguration {

    /**
     * jHipsterMetricsEndpoint.
     * @param meterRegistry a [io.micrometer.core.instrument.MeterRegistry] object.
     * @return a [EooMetricsEndpoint] object.
     */
    @Bean
    @ConditionalOnBean(MeterRegistry::class)
    @ConditionalOnMissingBean
    @ConditionalOnAvailableEndpoint
    fun eooMetricsEndpoint(meterRegistry: MeterRegistry): EooMetricsEndpoint {
        return EooMetricsEndpoint(meterRegistry)
    }
}
