package io.github.elieof.eoo.config.apidoc.customizer

import io.github.elieof.eoo.config.EooProperties
import org.springframework.core.Ordered
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import springfox.documentation.builders.PathSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.Contact
import springfox.documentation.service.Server
import springfox.documentation.spring.web.plugins.Docket
import java.nio.ByteBuffer

/**
 * A Springfox customizer to setup [Docket] with Eoo settings.
 */
class EooSpringfoxCustomizer(
    private val properties: EooProperties.ApiDocs,
    private var order: Int = DEFAULT_ORDER
) : SpringfoxCustomizer, Ordered {

    companion object {
        /** The default order for the customizer. */
        const val DEFAULT_ORDER = 0
    }

    override fun customize(docket: Docket) {
        val contact = Contact(properties.contactName, properties.contactUrl, properties.contactEmail)

        val apiInfo = ApiInfo(
            properties.title,
            properties.description,
            properties.version,
            properties.termsOfServiceUrl,
            contact,
            properties.license,
            properties.licenseUrl,
            emptyList()
        )

        for (server in properties.servers) {
            docket.servers(Server(server.name, server.url, server.description, emptyList(), emptyList()))
        }

        docket.host(properties.host)
            .protocols(properties.protocols.toSet())
            .apiInfo(apiInfo)
            .useDefaultResponseMessages(properties.useDefaultResponseMessages)
            .forCodeGeneration(true)
            .directModelSubstitute(ByteBuffer::class.java, String::class.java)
            .genericModelSubstitutes(ResponseEntity::class.java)
            .ignoredParameterTypes(Pageable::class.java)
            .select()
            .paths(PathSelectors.regex(properties.defaultIncludePattern))
            .build()
    }

    override fun getOrder(): Int {
        return order
    }
}
