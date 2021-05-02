package io.github.elieof.eoo.config.liquibase

import com.zaxxer.hikari.HikariDataSource
import io.github.elieof.eoo.config.EooProfiles
import io.github.elieof.eoo.test.LogbackRecorder
import org.assertj.core.api.Assertions
import org.assertj.core.api.InstanceOfAssertFactories
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.autoconfigure.liquibase.DataSourceClosingSpringLiquibase
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.task.SimpleAsyncTaskExecutor
import org.springframework.mock.env.MockEnvironment
import javax.sql.DataSource

internal class EooLiquibaseAutoConfigurationTest {

    private lateinit var executor: SimpleAsyncTaskExecutor
    private lateinit var environment: ConfigurableEnvironment
    private lateinit var config: EooLiquibaseAutoConfiguration
    private lateinit var recorder: LogbackRecorder

    @Mock
    private lateinit var dataSource: ObjectProvider<DataSource?>

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        executor = SimpleAsyncTaskExecutor()
        recorder = LogbackRecorder.forClass(MockEnvironment::class.java).reset().capture("ALL")
        environment = MockEnvironment()
        recorder.release()
        config = EooLiquibaseAutoConfiguration(environment)
        recorder = LogbackRecorder.forClass(EooLiquibaseAutoConfiguration::class.java).reset().capture("ALL")
        Mockito.`when`(dataSource.ifAvailable).thenReturn(null)
        Mockito.`when`(dataSource.ifUnique).thenReturn(null)
    }

    @AfterEach
    fun tearDown() {
        recorder.release()
    }

    @Test
    internal fun testLiquibase() {
        val liquibaseProperties = LiquibaseProperties()
        liquibaseProperties.url = URL_LIQUIBASE
        liquibaseProperties.user = USER_NAME
        val dataSourceProperties = DataSourceProperties()
        dataSourceProperties.password = PASSWORD
        val liquibase = config.liquibase(
            executor,
            dataSource,
            liquibaseProperties,
            dataSource,
            dataSourceProperties
        )

        Assertions.assertThat(liquibase).isNotNull
            .asInstanceOf(InstanceOfAssertFactories.type(DataSourceClosingSpringLiquibase::class.java))
            .hasFieldOrPropertyWithValue("shouldRun", liquibaseProperties.isEnabled)
            .hasFieldOrPropertyWithValue("changeLog", "classpath:config/liquibase/master.xml")
            .hasFieldOrPropertyWithValue("defaultSchema", liquibaseProperties.defaultSchema)
            .hasFieldOrPropertyWithValue("liquibaseSchema", liquibaseProperties.liquibaseSchema)
            .hasFieldOrPropertyWithValue("liquibaseTablespace", liquibaseProperties.liquibaseTablespace)
            .hasFieldOrPropertyWithValue("databaseChangeLogLockTable", liquibaseProperties.databaseChangeLogLockTable)
            .hasFieldOrPropertyWithValue("databaseChangeLogTable", liquibaseProperties.databaseChangeLogTable)
            .hasFieldOrPropertyWithValue("dropFirst", liquibaseProperties.isDropFirst)
            .hasFieldOrPropertyWithValue("labels", liquibaseProperties.labels)
            .hasFieldOrPropertyWithValue("testRollbackOnUpdate", liquibaseProperties.isTestRollbackOnUpdate)
            .extracting { it.dataSource }
            .asInstanceOf(InstanceOfAssertFactories.type(HikariDataSource::class.java))
            .hasFieldOrPropertyWithValue("jdbcUrl", URL_LIQUIBASE)
            .hasFieldOrPropertyWithValue("username", USER_NAME)
            .hasFieldOrPropertyWithValue("password", PASSWORD)

        val events = recorder.play()
        Assertions.assertThat(events).hasSize(1)
        val event: LogbackRecorder.Event = events[0]
        Assertions.assertThat(event.level).isEqualTo("DEBUG")
        Assertions.assertThat(event.message).isEqualTo(EooLiquibaseAutoConfiguration.STARTING_MESSAGE)
        Assertions.assertThat(event.thrown).isNull()
    }

    @Test
    fun testProfileNoLiquibase() {
        environment.setActiveProfiles(EooProfiles.SPRING_PROFILE_NO_LIQUIBASE)
        val liquibaseProperties = LiquibaseProperties()
        liquibaseProperties.url = URL_LIQUIBASE
        liquibaseProperties.user = USER_NAME
        val dataSourceProperties = DataSourceProperties()
        dataSourceProperties.password = PASSWORD

        config.liquibase(
            executor,
            dataSource,
            liquibaseProperties,
            dataSource,
            dataSourceProperties
        )

        val events = recorder.play()
        Assertions.assertThat(events).hasSize(1)
        val event: LogbackRecorder.Event = events[0]
        Assertions.assertThat(event.level).isEqualTo("DEBUG")
        Assertions.assertThat(event.message).isEqualTo(EooLiquibaseAutoConfiguration.DISABLED_MESSAGE)
        Assertions.assertThat(event.thrown).isNull()
    }
}
