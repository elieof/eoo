package io.github.elieof.eoo.config.metric

import io.micrometer.core.instrument.MeterRegistry
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(
    classes = [EooMetricsEndpointTest.TestApp::class],
)
@ActiveProfiles("test")
internal class EooMetricsEndpointTest(@Autowired val metricsEndpoint: EooMetricsEndpoint) {

    @Test
    fun processMetrics() {
        val result = metricsEndpoint.processMetrics()

        assertThat(result).isNotEmpty
        assertThat(result.keys)
            .isNotEmpty
            .allSatisfy {
                it.contains("cpu") || it.contains("system") || it.contains("process")
            }
    }

    @Test
    fun garbageCollectorMetrics() {
        val result = metricsEndpoint.garbageCollectorMetrics()

        assertThat(result).isNotEmpty
        assertThat(result.keys)
            .hasSizeGreaterThanOrEqualTo(5)
            .isNotEmpty
            .allSatisfy {
                it.contains("jvm.gc") || it.contains("classes")
            }
        assertThat((result.getValue("jvm.gc.pause") as Map<*, *>).keys).isNotEmpty
            .hasSizeGreaterThanOrEqualTo(5)
            .contains("count", "max", "mean", "totalTime")

        assertThat(result.values).isNotEmpty
    }

    @Test
    fun jvmMemoryMetrics() {
        val result = metricsEndpoint.jvmMemoryMetrics()

        assertThat(result).isNotEmpty
        assertThat(result.keys).hasSize(8).isNotEmpty
        assertThat(result.values).isNotEmpty
            .allMatch {
                it.keys.containsAll(setOf("used", "max", "committed")) && it.keys.size == 3
            }
    }

    @SpringBootApplication(
        exclude = [
            SecurityAutoConfiguration::class,
            ManagementWebSecurityAutoConfiguration::class,
            DataSourceAutoConfiguration::class,
            DataSourceTransactionManagerAutoConfiguration::class,
            HibernateJpaAutoConfiguration::class
        ]
    )
    class TestApp {

        @Bean
        fun eooMetricsEndpoint(meterRegistry: MeterRegistry): EooMetricsEndpoint {
            return EooMetricsEndpoint(meterRegistry)
        }
    }
}
