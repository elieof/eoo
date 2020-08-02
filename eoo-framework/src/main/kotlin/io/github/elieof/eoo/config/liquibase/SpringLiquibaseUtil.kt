package io.github.elieof.eoo.config.liquibase

import java.util.Optional
import java.util.function.Supplier
import javax.sql.DataSource
import liquibase.integration.spring.SpringLiquibase
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.autoconfigure.liquibase.DataSourceClosingSpringLiquibase
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties
import org.springframework.boot.jdbc.DataSourceBuilder

/**
 * Utility class for handling SpringLiquibase.
 *
 *
 * It follows implementation of
 * [LiquibaseAutoConfiguration](https://github.com/spring-projects/spring-boot/blob/master/spring-boot-project/spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/liquibase/LiquibaseAutoConfiguration.java)
 */
object SpringLiquibaseUtil {

    fun createSpringLiquibase(
        context: LiquibaseContext
    ): SpringLiquibase {
        val liquibase: SpringLiquibase
        val liquibaseDataSource = getDataSource(
            context.liquibaseDatasource, context.liquibaseProperties, context.dataSource
        )
        if (liquibaseDataSource != null) {
            liquibase = SpringLiquibase()
            liquibase.dataSource = liquibaseDataSource
            return liquibase
        }
        liquibase = DataSourceClosingSpringLiquibase()
        liquibase.setDataSource(
            createNewDataSource(context.liquibaseProperties, context.dataSourceProperties)
        )
        return liquibase
    }

    fun createAsyncSpringLiquibase(
        context: LiquibaseContext
    ): AsyncSpringLiquibase {
        val liquibase = AsyncSpringLiquibase(context.executor, context.env)
        val liquibaseDataSource = getDataSource(
            context.liquibaseDatasource, context.liquibaseProperties, context.dataSource
        )
        if (liquibaseDataSource != null) {
            liquibase.setCloseDataSourceOnceMigrated(false)
            liquibase.dataSource = liquibaseDataSource
        } else {
            liquibase.dataSource = createNewDataSource(
                context.liquibaseProperties,
                context.dataSourceProperties
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
            Supplier { liquibaseProperties.url },
            Supplier { dataSourceProperties.determineUrl() }
        )
        val user = getProperty(
            Supplier { liquibaseProperties.user },
            Supplier { dataSourceProperties.determineUsername() }
        )
        val password = getProperty(
            Supplier { liquibaseProperties.password },
            Supplier { dataSourceProperties.determinePassword() }
        )
        return DataSourceBuilder.create().url(url).username(user).password(password).build()
    }

    private fun getProperty(
        property: Supplier<String>,
        defaultValue: Supplier<String>
    ): String {
        return Optional.of(property)
            .map { obj: Supplier<String> -> obj.get() }
            .orElseGet(defaultValue)
    }
}
