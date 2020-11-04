package io.github.elieof.eoo.config.apidoc

import io.github.elieof.eoo.config.EooProfiles.SPRING_PROFILE_API_DOCS
import io.github.elieof.eoo.config.EooProperties
import io.github.elieof.eoo.config.logging.EooLoggingAutoConfiguration
import org.hamcrest.Matchers.hasItem
import org.hamcrest.Matchers.hasItems
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.bind.annotation.GetMapping

@SpringBootTest(
    classes = [SpringfoxAutoconfigurationTest.TestApp::class],
    properties = [
        "spring.liquibase.enabled=false",
        "security.basic.enabled=false",
        "eoo.api-docs.default-include-pattern=/scanned/.*",
        "eoo.api-docs.host=test.eoo.com",
        "eoo.api-docs.protocols=http,https",
        "eoo.api-docs.title=test title",
        "eoo.api-docs.description=test description",
        "eoo.api-docs.version=test version",
        "eoo.api-docs.terms-of-service-url=test tos url",
        "eoo.api-docs.contact-name=test contact name",
        "eoo.api-docs.contact-email=test contact email",
        "eoo.api-docs.contact-url=test contact url",
        "eoo.api-docs.license=test license name",
        "eoo.api-docs.license-url=test license url",
        "eoo.api-docs.servers[0].url=test server url",
        "management.endpoints.web.base-path=/management",
        "spring.application.name=testApp"
    ]
)
@ActiveProfiles(SPRING_PROFILE_API_DOCS)
@AutoConfigureMockMvc
class SpringfoxAutoconfigurationTest(
    @Autowired
    private val mockMvc: MockMvc
) {

    @Test
    @Throws(Exception::class)
    fun generatesOAS() {
        mockMvc.perform(get("/v3/api-docs"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.info.title").value("test title"))
            .andExpect(jsonPath("$.info.description").value("test description"))
            .andExpect(jsonPath("$.info.version").value("test version"))
            .andExpect(jsonPath("$.info.termsOfService").value("test tos url"))
            .andExpect(jsonPath("$.info.contact.name").value("test contact name"))
            .andExpect(jsonPath("$.info.contact.url").value("test contact url"))
            .andExpect(jsonPath("$.info.contact.email").value("test contact email"))
            .andExpect(jsonPath("$.info.license.name").value("test license name"))
            .andExpect(jsonPath("$.info.license.url").value("test license url"))
            .andExpect(jsonPath("$.paths./scanned/test").exists())
            .andExpect(jsonPath("$.paths./not-scanned/test").doesNotExist())
            // TODO: fix bug in Springfox
            // .andExpect(jsonPath("$.servers.[*].url").value(hasItem("test server url")))
            .andExpect(jsonPath("$.servers.[*].url").value(hasItem("http://localhost:80")))
    }

    @Test
    @Throws(Exception::class)
    fun generatesSwaggerV2() {
        mockMvc.perform(get("/v2/api-docs"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.paths./scanned/test").exists())
            .andExpect(jsonPath("$.host").value("test.eoo.com"))
            .andExpect(jsonPath("$.schemes").value(hasItems("http", "https")))
    }

    @Test
    @Throws(Exception::class)
    fun setsPageParameters() {
        mockMvc.perform(get("/v3/api-docs"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.paths./scanned/test.get.parameters[?(@.name == 'page')]").exists())
            .andExpect(jsonPath("$.paths./scanned/test.get.parameters[?(@.name == 'page')].in").value("query"))
            .andExpect(jsonPath("$.paths./scanned/test.get.parameters[?(@.name == 'page')].schema.type").value("integer"))
            .andExpect(jsonPath("$.paths./scanned/test.get.parameters[?(@.name == 'size')]").exists())
            .andExpect(jsonPath("$.paths./scanned/test.get.parameters[?(@.name == 'size')].in").value("query"))
            .andExpect(jsonPath("$.paths./scanned/test.get.parameters[?(@.name == 'size')].schema.type").value("integer"))
            .andExpect(jsonPath("$.paths./scanned/test.get.parameters[?(@.name == 'sort')]").exists())
            .andExpect(jsonPath("$.paths./scanned/test.get.parameters[?(@.name == 'sort')].in").value("query"))
            .andExpect(jsonPath("$.paths./scanned/test.get.parameters[?(@.name == 'sort')].schema.type").value("array"))
            .andExpect(jsonPath("$.paths./scanned/test.get.parameters[?(@.name == 'sort')].schema.items.type").value("string"))
    }

    @Test
    @Throws(Exception::class)
    fun generatesManagementOAS() {
        mockMvc.perform(get("/v3/api-docs?group=management"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.info.title").value("TestApp Management API"))
            .andExpect(jsonPath("$.info.description").value("Management endpoints documentation"))
            .andExpect(jsonPath("$.info.version").value("test version"))
            .andExpect(jsonPath("$.info.termsOfService").doesNotExist())
            .andExpect(jsonPath("$.info.contact").isEmpty)
            .andExpect(jsonPath("$.info.license").isEmpty)
            .andExpect(jsonPath("$.paths./management/health").exists())
            .andExpect(jsonPath("$.paths./scanned/test").doesNotExist())
            // TODO: fix bug in Springfox
            // .andExpect(jsonPath("$.servers.[*].url").value(hasItem("test server url")))
            .andExpect(jsonPath("$.servers.[*].url").value(hasItem("http://localhost:80")))
    }

    @Test
    @Throws(Exception::class)
    fun generatesManagementSwaggerV2() {
        mockMvc.perform(get("/v2/api-docs?group=management"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.paths./management/health").exists())
            .andExpect(jsonPath("$.host").value("test.eoo.com"))
            .andExpect(jsonPath("$.schemes").value(hasItems("http", "https")))
    }

    @SpringBootApplication(
        scanBasePackages = ["io.github.elieof.eoo.config.apidoc"],
        exclude = [
            SecurityAutoConfiguration::class,
            ManagementWebSecurityAutoConfiguration::class,
            DataSourceAutoConfiguration::class,
            DataSourceTransactionManagerAutoConfiguration::class,
            HibernateJpaAutoConfiguration::class,
            EooLoggingAutoConfiguration::class
        ]
    )
    @Controller
    @EnableConfigurationProperties(EooProperties::class)
    internal class TestApp {

        @GetMapping("/scanned/test")
        fun scanned(pageable: Pageable?) {
        }

        @GetMapping("/not-scanned/test")
        fun notscanned(pageable: Pageable?) {
        }
    }
}
