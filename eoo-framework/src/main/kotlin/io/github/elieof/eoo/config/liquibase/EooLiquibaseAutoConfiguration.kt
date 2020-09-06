package io.github.elieof.eoo.config.liquibase

import io.github.elieof.eoo.config.EooProfiles
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.core.env.Profiles
import java.util.concurrent.Executor
import javax.sql.DataSource

/**
 * Auto configuration for Liquibase using changelog 'classpath:config/liquibase/master.xml'
 * and based on [LiquibaseAutoConfiguration].
 * Depends on profile [EooProfiles.SPRING_PROFILE_NO_LIQUIBASE]
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(LiquibaseAutoConfiguration::class)
@EnableConfigurationProperties(LiquibaseProperties::class)
class EooLiquibaseAutoConfiguration(val environment: Environment) {

    companion object {

        private val logger = LoggerFactory.getLogger(EooLiquibaseAutoConfiguration::class.java)

        /** Constant `DISABLED_MESSAGE="Liquibase is disabled"`  */
        const val DISABLED_MESSAGE = "Liquibase is disabled"

        /** Constant `DISABLED_MESSAGE="Liquibase is disabled"`  */
        const val STARTING_MESSAGE = "Configuring Liquibase"
    }

    @Bean
    fun liquibase(
        @Qualifier("taskExecutor") executor: Executor,
        liquibaseDataSource: ObjectProvider<DataSource?>,
        liquibaseProperties: LiquibaseProperties,
        dataSource: ObjectProvider<DataSource?>,
        dataSourceProperties: DataSourceProperties
    ) =
    // If you don't want Liquibase to start asynchronously, substitute by this:
        // SpringLiquibase liquibase = SpringLiquibaseUtil.createSpringLiquibase(liquibaseDataSource.getIfAvailable(), liquibaseProperties, dataSource.getIfUnique(), dataSourceProperties);
        SpringLiquibaseUtil.createAsyncSpringLiquibase(
            liquibaseDataSource.ifAvailable,
            liquibaseProperties,
            dataSource.ifUnique,
            dataSourceProperties,
            environment,
            executor
        ).apply {
            changeLog = "classpath:config/liquibase/master.xml"
            contexts = liquibaseProperties.contexts
            defaultSchema = liquibaseProperties.defaultSchema
            liquibaseSchema = liquibaseProperties.liquibaseSchema
            liquibaseTablespace = liquibaseProperties.liquibaseTablespace
            databaseChangeLogLockTable = liquibaseProperties.databaseChangeLogLockTable
            databaseChangeLogTable = liquibaseProperties.databaseChangeLogTable
            isDropFirst = liquibaseProperties.isDropFirst
            labels = liquibaseProperties.labels
            setChangeLogParameters(liquibaseProperties.parameters)
            setRollbackFile(liquibaseProperties.rollbackFile)
            isTestRollbackOnUpdate = liquibaseProperties.isTestRollbackOnUpdate

            if (environment.acceptsProfiles(Profiles.of(EooProfiles.SPRING_PROFILE_NO_LIQUIBASE))) {
                logger.debug(DISABLED_MESSAGE)
                setShouldRun(false)
            } else {
                setShouldRun(liquibaseProperties.isEnabled)
                logger.debug(STARTING_MESSAGE)
            }
        }
}
