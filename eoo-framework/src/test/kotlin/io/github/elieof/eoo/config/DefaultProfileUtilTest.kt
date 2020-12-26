package io.github.elieof.eoo.config

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.boot.SpringApplication
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

internal class DefaultProfileUtilTest{

    @Test
    internal fun testAddDefaultProfile() {
        val app = SpringApplication()
        Assertions.assertThat(EooProfiles).isNotNull
        Assertions.assertThat(DefaultProfileUtil).isNotNull
        DefaultProfileUtil.addDefaultProfile(app)
        val defaultProperties = SpringApplication::class.memberProperties.find { it.name == "defaultProperties" }
        defaultProperties?.let {
            it.isAccessible = true
            val value = it.get(app) as MutableMap<String, *>
            Assertions.assertThat(value).isNotNull.isNotEmpty
            Assertions.assertThat(value).containsEntry(
                DefaultProfileUtil.SPRING_PROFILE_DEFAULT, EooProfiles.SPRING_PROFILE_DEVELOPMENT)
        }
    }
}
