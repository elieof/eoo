package io.github.elieof.eoo.config.liquibase

import java.util.concurrent.Executor
import javax.sql.DataSource
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties
import org.springframework.core.env.Environment

class LiquibaseContext(
    val liquibaseDatasource: DataSource? = null,
    val liquibaseProperties: LiquibaseProperties,
    val dataSource: DataSource? = null,
    val dataSourceProperties: DataSourceProperties,
    val env: Environment? = null,
    val executor: Executor? = null
)
