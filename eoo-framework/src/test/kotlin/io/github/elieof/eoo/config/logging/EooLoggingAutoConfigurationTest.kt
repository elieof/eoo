package io.github.elieof.eoo.config.logging

import io.github.elieof.eoo.config.EooProperties
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration
import org.zalando.logbook.Sink
import org.zalando.logbook.autoconfigure.LogbookAutoConfiguration
import org.zalando.logbook.logstash.LogstashLogbackSink

class EooLoggingAutoConfigurationTest {
    private val contextRunner = ApplicationContextRunner()

    @EnableConfigurationProperties(EooProperties::class)
    internal class TrivialConfiguration

    @Test
    fun someTest() {
        this.contextRunner
            .withPropertyValues("eoo.logging.logstash.enabled=true")
            .withAllowBeanDefinitionOverriding(true)
            .withUserConfiguration(
                TrivialConfiguration::class.java,
                RefreshAutoConfiguration::class.java,
                JacksonAutoConfiguration::class.java,
                LogbookAutoConfiguration::class.java,
                EooLoggingAutoConfiguration::class.java
            )
            .run {
                Assertions.assertThat(it).hasSingleBean(Sink::class.java)
                Assertions.assertThat(it.getBean(Sink::class.java)).isInstanceOf(LogstashLogbackSink::class.java)
            }
    }
}
