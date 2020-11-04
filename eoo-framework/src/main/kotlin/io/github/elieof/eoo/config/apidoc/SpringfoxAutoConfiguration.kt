package io.github.elieof.eoo.config.apidoc

import io.github.elieof.eoo.config.EooProfiles.SPRING_PROFILE_API_DOCS
import io.github.elieof.eoo.config.EooProperties
import io.github.elieof.eoo.config.apidoc.customizer.EooSpringfoxCustomizer
import io.github.elieof.eoo.config.apidoc.customizer.SpringfoxCustomizer
import mu.KotlinLogging
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Profile
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.util.StopWatch
import org.springframework.util.StringUtils
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration
import springfox.documentation.builders.PathSelectors
import springfox.documentation.oas.configuration.OpenApiDocumentationConfiguration
import springfox.documentation.schema.AlternateTypeRule
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.Server
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.configuration.Swagger2DocumentationConfiguration
import java.nio.ByteBuffer
import java.util.*

private val logger = KotlinLogging.logger {}

/**
 * Springfox OpenAPI configuration.
 * <p>
 * Warning! When having a lot of REST endpoints, Springfox can become a performance issue.
 * In that case, you can use the "no-api-docs" Spring profile, so that this bean is ignored.
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass(ApiInfo::class, BeanValidatorPluginsConfiguration::class, Docket::class)
@Profile(SPRING_PROFILE_API_DOCS)
@AutoConfigureAfter(EooProperties::class)
@Import(
    OpenApiDocumentationConfiguration::class,
    Swagger2DocumentationConfiguration::class,
    BeanValidatorPluginsConfiguration::class
)
public class SpringfoxAutoConfiguration(eooProperties: EooProperties) {

    public companion object {
        private const val STARTING_MESSAGE: String = "Starting OpenAPI docs"
        private const val STARTED_MESSAGE: String = "Started OpenAPI docs in {} ms"
        private const val MANAGEMENT_TITLE_SUFFIX: String = "Management API"
        private const val MANAGEMENT_GROUP_NAME: String = "management"
        private const val MANAGEMENT_DESCRIPTION: String = "Management endpoints documentation"
    }

    private val properties = eooProperties.apiDocs

    /**
     * Springfox configuration for the OpenAPI docs.
     *
     * @param springfoxCustomizers Springfox customizers
     * @param alternateTypeRules alternate type rules
     * @return the Springfox configuration
     */
    @Bean
    @ConditionalOnMissingBean(name = ["openAPISpringfoxApiDocket"])
    public fun openAPISpringfoxApiDocket(
        springfoxCustomizers: List<SpringfoxCustomizer>,
        alternateTypeRules: ObjectProvider<Array<AlternateTypeRule>>
    ): Docket? {
        logger.debug(STARTING_MESSAGE)
        val watch = StopWatch()
        watch.start()
        val docket = createDocket()

        // Apply all OpenAPICustomizers orderly.
        springfoxCustomizers.forEach {
            it.customize(docket)
        }

        // Add all AlternateTypeRules if available in spring bean factory.
        // Also you can add your rules in a customizer bean above.
        Optional.ofNullable(alternateTypeRules.ifAvailable)
            .ifPresent(docket::alternateTypeRules)
        watch.stop()
        logger.debug(STARTED_MESSAGE, watch.totalTimeMillis)
        return docket
    }

    /**
     * JHipster Springfox Customizer
     *
     * @return the Sringfox Customizer of JHipster
     */
    @Bean
    public fun eooSpringfoxCustomizer(): EooSpringfoxCustomizer? {
        return EooSpringfoxCustomizer(properties)
    }

    /**
     * Springfox configuration for the management endpoints (actuator) OpenAPI docs.
     *
     * @param appName               the application name
     * @param managementContextPath the path to access management endpoints
     * @return the Springfox configuration
     */
    @Bean
    @ConditionalOnClass(name = ["org.springframework.boot.actuate.autoconfigure.web.server.ManagementServerProperties"])
    @ConditionalOnProperty("management.endpoints.web.base-path")
    @ConditionalOnExpression("'\${management.endpoints.web.base-path}'.length() > 0")
    @ConditionalOnMissingBean(name = ["openAPISpringfoxManagementDocket"])
    public fun openAPISpringfoxManagementDocket(
        @Value("\${spring.application.name:application:eooApp}") appName: String,
        @Value("\${management.endpoints.web.base-path}") managementContextPath: String
    ): Docket {
        val apiInfo = ApiInfo(
            StringUtils.capitalize(appName) + " " + MANAGEMENT_TITLE_SUFFIX,
            MANAGEMENT_DESCRIPTION,
            properties.version,
            "",
            ApiInfo.DEFAULT_CONTACT,
            "",
            "",
            emptyList()
        )
        val docket = createDocket()
        for (server in properties.servers) {
            docket.servers(Server(server.name, server.url, server.description, emptyList(), emptyList()))
        }
        return docket
            .apiInfo(apiInfo)
            .useDefaultResponseMessages(properties.useDefaultResponseMessages)
            .groupName(MANAGEMENT_GROUP_NAME)
            .host(properties.host)
            .protocols(properties.protocols.toSet())
            .forCodeGeneration(true)
            .directModelSubstitute(ByteBuffer::class.java, String::class.java)
            .genericModelSubstitutes(ResponseEntity::class.java)
            .ignoredParameterTypes(Pageable::class.java)
            .select()
            .paths(PathSelectors.regex("$managementContextPath.*"))
            .build()
    }

    /**
     *
     * createDocket.
     *
     * @return a [springfox.documentation.spring.web.plugins.Docket] object.
     */
    protected fun createDocket(): Docket {
        return Docket(DocumentationType.OAS_30)
    }
}
