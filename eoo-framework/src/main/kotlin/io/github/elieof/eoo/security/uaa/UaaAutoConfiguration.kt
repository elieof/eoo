package io.github.elieof.eoo.security.uaa


import io.github.elieof.eoo.config.EooProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails


/**
 *
 * UaaAutoConfiguration class.
 */
@Configuration
@ConditionalOnClass(
    ClientCredentialsResourceDetails::class,
    LoadBalancerClient::class
)
@ConditionalOnProperty("eoo.security.client-authorization.client-id")
class UaaAutoConfiguration(val eooProperties: EooProperties) {

    /**
     *
     * loadBalancedResourceDetails.
     *
     * @param loadBalancerClient a [org.springframework.cloud.client.loadbalancer.LoadBalancerClient] object.
     * @return a [io.github.jhipster.security.uaa.LoadBalancedResourceDetails] object.
     */
    @Bean
    fun loadBalancedResourceDetails(loadBalancerClient: LoadBalancerClient): LoadBalancedResourceDetails {
        val clientAuthorization: EooProperties.Security.ClientAuthorization = eooProperties.security.clientAuthorization
        val loadBalancedResourceDetails = LoadBalancedResourceDetails(
            loadBalancerClient, clientAuthorization.tokenServiceId
        )
        loadBalancedResourceDetails.accessTokenUri = clientAuthorization.accessTokenUri
        loadBalancedResourceDetails.clientId = clientAuthorization.clientId
        loadBalancedResourceDetails.clientSecret = clientAuthorization.clientSecret
        return loadBalancedResourceDetails
    }
}

