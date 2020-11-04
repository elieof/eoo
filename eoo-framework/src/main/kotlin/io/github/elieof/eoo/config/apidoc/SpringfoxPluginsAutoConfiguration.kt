package io.github.elieof.eoo.config.apidoc

import com.fasterxml.classmate.TypeResolver
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.Pageable
import springfox.documentation.spring.web.plugins.Docket

/**
 * Register Springfox plugins.
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnBean(Docket::class)
@AutoConfigureAfter(SpringfoxAutoConfiguration::class)
class SpringfoxPluginsAutoConfiguration {

    @Configuration
    @ConditionalOnClass(Pageable::class)
    class SpringPagePluginConfiguration {

        @Bean
        @ConditionalOnMissingBean
        fun pageableParameterBuilderPlugin(typeResolver: TypeResolver): PageableParameterBuilderPlugin {
            return PageableParameterBuilderPlugin(typeResolver)
        }
    }
}
