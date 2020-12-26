package io.github.elieof.eoo.security.ssl

import io.undertow.Undertow
import io.undertow.UndertowOptions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory
import org.springframework.test.util.ReflectionTestUtils
import org.xnio.OptionMap

class UndertowSSLConfigurationTest {

    @Test
    fun testUndertowSSLConfigurationOK() {
        // Prepare
        val undertowServletWebServerFactory = UndertowServletWebServerFactory()

        // Execute
        UndertowSSLConfiguration(undertowServletWebServerFactory)

        // Verify
        val builder = Undertow.builder()
        undertowServletWebServerFactory.builderCustomizers.forEach {
            it.customize(builder)
        }
        val serverOptions = ReflectionTestUtils.getField(builder, "socketOptions") as OptionMap.Builder
        assertThat(undertowServletWebServerFactory).isNotNull
        assertThat(serverOptions.map.get(UndertowOptions.SSL_USER_CIPHER_SUITES_ORDER)).isTrue
    }
}
