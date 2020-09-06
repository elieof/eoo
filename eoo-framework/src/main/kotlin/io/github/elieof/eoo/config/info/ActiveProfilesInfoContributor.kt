package io.github.elieof.eoo.config.info

import org.springframework.boot.actuate.info.Info
import org.springframework.boot.actuate.info.InfoContributor
import org.springframework.core.env.ConfigurableEnvironment

/**
 * An [InfoContributor] that exposes the list of active profiles.
 */
class ActiveProfilesInfoContributor(environment: ConfigurableEnvironment) : InfoContributor {
    private var profiles: List<String> = environment.activeProfiles.toList()

    companion object {
        private val ACTIVE_PROFILES = "activeProfiles"
    }

    override fun contribute(builder: Info.Builder) {
        builder.withDetail(ACTIVE_PROFILES, this.profiles)
    }
}
