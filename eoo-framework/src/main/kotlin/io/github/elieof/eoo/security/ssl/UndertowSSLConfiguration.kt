package io.github.elieof.eoo.security.ssl

import io.undertow.UndertowOptions
import mu.KotlinLogging
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.web.embedded.undertow.UndertowBuilderCustomizer
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory
import org.springframework.context.annotation.Configuration

private val logger = KotlinLogging.logger {}

/**
 * SSL configuration for Undertow.
 *
 *
 * SSL_USER_CIPHER_SUITES_ORDER : It will force the cipher suite defined by the user,
 * allowing to achieve perfect forward secrecy.
 * This can only be activated with HTTPS and a cipher suite defined by the user (server.ssl.ciphers).
 *
 *
 * Please note that when using Eoo, you can use the `server.ssl.ciphers` property that is available
 * in your `application-tls.yml` file, and which is ready to work with this configuration.
 *
 * [More explanation on perfect forward secrecy](https://github.com/ssllabs/research/wiki/SSL-and-TLS-Deployment-Best-Practices#25-use-forward-secrecy)
 */
@Configuration
@ConditionalOnBean(UndertowServletWebServerFactory::class)
@ConditionalOnClass(
    UndertowOptions::class
)
@ConditionalOnProperty("server.ssl.ciphers", "server.ssl.key-store")
class UndertowSSLConfiguration(private val factory: UndertowServletWebServerFactory) {

    init {
        configuringUserCipherSuiteOrder()
    }

    private fun configuringUserCipherSuiteOrder() {
        logger.info { "Configuring Undertow" }
        logger.info("Setting user cipher suite order to true")
        factory.addBuilderCustomizers(
            UndertowBuilderCustomizer {
                it.setSocketOption(UndertowOptions.SSL_USER_CIPHER_SUITES_ORDER, true)
            }
        )
    }
}
