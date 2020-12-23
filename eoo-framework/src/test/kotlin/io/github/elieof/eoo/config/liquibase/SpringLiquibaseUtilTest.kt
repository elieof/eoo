package io.github.elieof.eoo.config.liquibase

import com.zaxxer.hikari.HikariDataSource
import liquibase.integration.spring.SpringLiquibase
import org.assertj.core.api.Assertions
import org.assertj.core.api.InstanceOfAssertFactories.type
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.autoconfigure.liquibase.DataSourceClosingSpringLiquibase
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.core.task.SimpleAsyncTaskExecutor
import org.springframework.mock.env.MockEnvironment

const val URL_LIQUIBASE = "jdbc:h2:mem:liquibase"
const val URL_NORMAL = "jdbc:h2:mem:normal"
const val USER_NAME = "sa"
const val PASSWORD = "password"

class SpringLiquibaseUtilTest {

    private val liquibaseDataSource = DataSourceBuilder.create().url(URL_LIQUIBASE).username(USER_NAME).build()

    private val normalDataSource = DataSourceBuilder.create().url(URL_NORMAL).username(USER_NAME).build()

    private val env = MockEnvironment()

    private val executor = SimpleAsyncTaskExecutor()

    @Test
    fun createSpringLiquibaseFromLiquibaseDataSource() {
        val liquibase: SpringLiquibase = SpringLiquibaseUtil.createSpringLiquibase(
            liquibaseDataSource,
            LiquibaseProperties(),
            null,
            DataSourceProperties()
        )
        Assertions.assertThat(liquibase)
            .isNotInstanceOf(DataSourceClosingSpringLiquibase::class.java)
            .extracting { it.dataSource }
            .isEqualTo(liquibaseDataSource)
            .asInstanceOf(type(HikariDataSource::class.java))
            .hasFieldOrPropertyWithValue("jdbcUrl", URL_LIQUIBASE)
            .hasFieldOrPropertyWithValue("username", USER_NAME)
            .hasFieldOrPropertyWithValue("password", null)
    }

    @Test
    fun createSpringLiquibaseFromNormalDataSource() {
        val liquibase: SpringLiquibase = SpringLiquibaseUtil.createSpringLiquibase(
            null,
            LiquibaseProperties(),
            normalDataSource,
            DataSourceProperties()
        )
        Assertions.assertThat(liquibase)
            .isNotInstanceOf(DataSourceClosingSpringLiquibase::class.java)
            .extracting { it.dataSource }
            .isEqualTo(normalDataSource)
            .asInstanceOf(type(HikariDataSource::class.java))
            .hasFieldOrPropertyWithValue("jdbcUrl", URL_NORMAL)
            .hasFieldOrPropertyWithValue("username", USER_NAME)
            .hasFieldOrPropertyWithValue("password", null)
    }

    @Test
    fun createSpringLiquibaseFromLiquibaseProperties() {
        val liquibaseProperties = LiquibaseProperties()
        liquibaseProperties.url = URL_LIQUIBASE
        liquibaseProperties.user = USER_NAME
        val dataSourceProperties = DataSourceProperties()
        dataSourceProperties.password = PASSWORD

        val liquibase: SpringLiquibase = SpringLiquibaseUtil.createSpringLiquibase(
            null,
            liquibaseProperties,
            null,
            dataSourceProperties
        )
        Assertions.assertThat(liquibase)
            .asInstanceOf(type(DataSourceClosingSpringLiquibase::class.java))
            .extracting { it.dataSource }
            .asInstanceOf(type(HikariDataSource::class.java))
            .hasFieldOrPropertyWithValue("jdbcUrl", URL_LIQUIBASE)
            .hasFieldOrPropertyWithValue("username", USER_NAME)
            .hasFieldOrPropertyWithValue("password", PASSWORD)
    }

    @Test
    fun createSpringLiquibaseFromLiquibaseUrlAndDatasourceProperties() {
        val liquibaseProperties = LiquibaseProperties()
        liquibaseProperties.url = URL_LIQUIBASE
        val dataSourceProperties = DataSourceProperties()
        dataSourceProperties.password = PASSWORD
        dataSourceProperties.username = USER_NAME

        val liquibase: SpringLiquibase = SpringLiquibaseUtil.createSpringLiquibase(
            null,
            liquibaseProperties,
            null,
            dataSourceProperties
        )
        Assertions.assertThat(liquibase)
            .asInstanceOf(type(DataSourceClosingSpringLiquibase::class.java))
            .extracting { it.dataSource }
            .asInstanceOf(type(HikariDataSource::class.java))
            .hasFieldOrPropertyWithValue("jdbcUrl", URL_LIQUIBASE)
            .hasFieldOrPropertyWithValue("username", USER_NAME)
            .hasFieldOrPropertyWithValue("password", PASSWORD)
    }

    @Test
    fun createSpringLiquibaseFromLiquibaseUserAndDatasourceProperties() {
        val liquibaseProperties = LiquibaseProperties()
        liquibaseProperties.user = USER_NAME
        val dataSourceProperties = DataSourceProperties()
        dataSourceProperties.password = PASSWORD
        dataSourceProperties.url = URL_LIQUIBASE

        val liquibase: SpringLiquibase = SpringLiquibaseUtil.createSpringLiquibase(
            null,
            liquibaseProperties,
            null,
            dataSourceProperties
        )
        Assertions.assertThat(liquibase)
            .asInstanceOf(type(DataSourceClosingSpringLiquibase::class.java))
            .extracting { it.dataSource }
            .asInstanceOf(type(HikariDataSource::class.java))
            .hasFieldOrPropertyWithValue("jdbcUrl", URL_LIQUIBASE)
            .hasFieldOrPropertyWithValue("username", USER_NAME)
            .hasFieldOrPropertyWithValue("password", PASSWORD)
    }

    @Test
    fun createAsyncSpringLiquibaseFromLiquibaseDataSource() {
        val liquibaseProperties = LiquibaseProperties()
        val dataSourceProperties = DataSourceProperties()

        val liquibase: SpringLiquibase = SpringLiquibaseUtil.createAsyncSpringLiquibase(
            liquibaseDataSource,
            liquibaseProperties,
            null,
            dataSourceProperties,
            env,
            executor
        )
        Assertions.assertThat(liquibase.dataSource)
            .isEqualTo(liquibaseDataSource)
            .asInstanceOf(type(HikariDataSource::class.java))
            .hasFieldOrPropertyWithValue("jdbcUrl", URL_LIQUIBASE)
            .hasFieldOrPropertyWithValue("username", USER_NAME)
            .hasFieldOrPropertyWithValue("password", null)
    }

    @Test
    fun createAsyncSpringLiquibaseFromNormalDataSource() {
        val liquibaseProperties = LiquibaseProperties()
        val dataSourceProperties = DataSourceProperties()

        val liquibase: SpringLiquibase = SpringLiquibaseUtil.createAsyncSpringLiquibase(
            null,
            liquibaseProperties,
            normalDataSource,
            dataSourceProperties,
            env,
            executor
        )
        Assertions.assertThat(liquibase.dataSource)
            .isEqualTo(normalDataSource)
            .asInstanceOf(type(HikariDataSource::class.java))
            .hasFieldOrPropertyWithValue("jdbcUrl", URL_NORMAL)
            .hasFieldOrPropertyWithValue("username", USER_NAME)
            .hasFieldOrPropertyWithValue("password", null)
    }

    @Test
    fun createAsyncSpringLiquibaseFromLiquibaseProperties() {
        val liquibaseProperties = LiquibaseProperties()
        liquibaseProperties.url = URL_LIQUIBASE
        liquibaseProperties.user = USER_NAME
        val dataSourceProperties = DataSourceProperties()
        dataSourceProperties.password = PASSWORD

        val liquibase: SpringLiquibase = SpringLiquibaseUtil.createAsyncSpringLiquibase(
            null,
            liquibaseProperties,
            null,
            dataSourceProperties,
            env,
            executor
        )
        Assertions.assertThat(liquibase)
            .asInstanceOf(type(DataSourceClosingSpringLiquibase::class.java))
            .extracting { it.dataSource }
            .asInstanceOf(type(HikariDataSource::class.java))
            .hasFieldOrPropertyWithValue("jdbcUrl", URL_LIQUIBASE)
            .hasFieldOrPropertyWithValue("username", USER_NAME)
            .hasFieldOrPropertyWithValue("password", PASSWORD)
    }
}
