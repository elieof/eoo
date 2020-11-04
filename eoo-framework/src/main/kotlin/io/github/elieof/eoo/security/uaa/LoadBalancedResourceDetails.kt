package io.github.elieof.eoo.security.uaa

import mu.KotlinLogging
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails
import java.net.URI
import java.net.URISyntaxException

private val logger = KotlinLogging.logger {}

/**
 *
 * LoadBalancedResourceDetails class.
 */
@ConditionalOnMissingBean
public class LoadBalancedResourceDetails(
    private val loadBalancerClient: LoadBalancerClient,
    _tokenServiceId: String = "",
) : ClientCredentialsResourceDetails() {
    public companion object {
        /** Constant `EXCEPTION_MESSAGE="Returning an invalid URI: {}"`  */
        public const val EXCEPTION_MESSAGE: String = "Returning an invalid URI: {}"
    }

    public val tokenServiceId: String = _tokenServiceId

    /** {@inheritDoc}  */
    override fun getAccessTokenUri(): String {
        return if (tokenServiceId.isNotEmpty()) {
            try {
                loadBalancerClient.reconstructURI(
                    loadBalancerClient.choose(tokenServiceId),
                    URI(super.getAccessTokenUri())
                ).toString()
            } catch (e: URISyntaxException) {
                logger.error(EXCEPTION_MESSAGE, e.message)
                super.getAccessTokenUri()
            }
        } else {
            super.getAccessTokenUri()
        }
    }

}
