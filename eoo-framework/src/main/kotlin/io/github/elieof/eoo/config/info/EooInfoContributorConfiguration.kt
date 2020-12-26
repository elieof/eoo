package io.github.elieof.eoo.config.info

import org.springframework.boot.actuate.autoconfigure.info.ConditionalOnEnabledInfoContributor
import org.springframework.context.annotation.Bean
import org.springframework.core.env.ConfigurableEnvironment

class EooInfoContributorConfiguration {

    /**
     *
     * activeProfilesInfoContributor.
     *
     * @param environment a [org.springframework.core.env.ConfigurableEnvironment] object.
     * @return a [ActiveProfilesInfoContributor] object.
     */
    @Bean
    @ConditionalOnEnabledInfoContributor("active-profiles")
    fun activeProfilesInfoContributor(environment: ConfigurableEnvironment): ActiveProfilesInfoContributor {
        return ActiveProfilesInfoContributor(environment)
    }
}
