package io.github.elieof.eoo.config.liquibase

import liquibase.integration.spring.SpringLiquibase
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.autoconfigure.liquibase.DataSourceClosingSpringLiquibase
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.core.env.Environment
import java.util.Optional
import java.util.concurrent.Executor
import java.util.function.Supplier
import javax.sql.DataSource

/**
 * Utility class for handling SpringLiquibase.
 *
 * It follows implementation of
 * [LiquibaseAutoConfiguration](https://github.com/spring-projects/spring-boot/blob/master/spring-boot-project/spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/liquibase/LiquibaseAutoConfiguration.java)
 */
object SpringLiquibaseUtil {

    fun createSpringLiquibase(
        liquibaseDatasource: DataSource?,
        liquibaseProperties: LiquibaseProperties,
        dataSource: DataSource?,
        dataSourceProperties: DataSourceProperties
    ): SpringLiquibase {
        val liquibase: SpringLiquibase
        val liquibaseDataSource = getDataSource(
            liquibaseDatasource,
            liquibaseProperties,
            dataSource
        )
        if (liquibaseDataSource != null) {
            liquibase = SpringLiquibase()
            liquibase.dataSource = liquibaseDataSource
            return liquibase
        }
        liquibase = DataSourceClosingSpringLiquibase()
        liquibase.setDataSource(
            createNewDataSource(liquibaseProperties, dataSourceProperties)
        )
        return liquibase
    }

    @Suppress("LongParameterList")
    fun createAsyncSpringLiquibase(
        liquibaseDatasource: DataSource?,
        liquibaseProperties: LiquibaseProperties,
        dataSource: DataSource?,
        dataSourceProperties: DataSourceProperties,
        env: Environment,
        executor: Executor
    ): AsyncSpringLiquibase {
        val liquibase = AsyncSpringLiquibase(executor, env)
        val liquibaseDataSource = getDataSource(
            liquibaseDatasource,
            liquibaseProperties,
            dataSource
        )
        if (liquibaseDataSource != null) {
            liquibase.setCloseDataSourceOnceMigrated(false)
            liquibase.dataSource = liquibaseDataSource
        } else {
            liquibase.dataSource = createNewDataSource(
                liquibaseProperties,
                dataSourceProperties
            )
        }
        return liquibase
    }

    private fun getDataSource(
        liquibaseDataSource: DataSource?,
        liquibaseProperties: LiquibaseProperties,
        dataSource: DataSource?
    ): DataSource? {
        if (liquibaseDataSource != null) {
            return liquibaseDataSource
        }
        return if (liquibaseProperties.url == null && liquibaseProperties.user == null) {
            dataSource
        } else null
    }

    private fun createNewDataSource(
        liquibaseProperties: LiquibaseProperties,
        dataSourceProperties: DataSourceProperties
    ): DataSource? {
        val url = getProperty(
            { liquibaseProperties.url },
            { dataSourceProperties.determineUrl() }
        )
        val user = getProperty(
            { liquibaseProperties.user },
            { dataSourceProperties.determineUsername() }
        )
        val password = getProperty(
            { liquibaseProperties.password },
            { dataSourceProperties.determinePassword() }
        )
        return DataSourceBuilder.create().url(url).username(user).password(password).build()
    }

    private fun getProperty(property: Supplier<String>, defaultValue: Supplier<String>): String {
        return Optional.of(property)
            .map { obj: Supplier<String> -> obj.get() }
            .orElseGet(defaultValue)
    }
}
