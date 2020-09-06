package io.github.elieof.eoo.config.liquibase

import io.github.elieof.eoo.config.EooProfiles.SPRING_PROFILE_DEVELOPMENT
import io.github.elieof.eoo.config.EooProfiles.SPRING_PROFILE_HEROKU
import io.github.elieof.eoo.config.EooProfiles.SPRING_PROFILE_NO_LIQUIBASE
import io.github.elieof.eoo.config.EooProfiles.SPRING_PROFILE_PRODUCTION
import io.github.elieof.eoo.config.liquibase.AsyncSpringLiquibase.Companion.NUMBER_1000L
import io.github.elieof.eoo.config.liquibase.AsyncSpringLiquibase.Companion.SLOWNESS_THRESHOLD
import io.github.elieof.eoo.test.LogbackRecorder
import io.github.elieof.eoo.test.LogbackRecorder.Event
import liquibase.Liquibase
import liquibase.exception.LiquibaseException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.verify
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.Environment
import org.springframework.core.task.SimpleAsyncTaskExecutor
import org.springframework.core.task.TaskExecutor
import org.springframework.mock.env.MockEnvironment
import java.sql.Connection
import java.sql.SQLException
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import javax.sql.DataSource
import kotlin.concurrent.withLock

class AsyncSpringLiquibaseTest {

    private val exception = LiquibaseException("Error!")

    private lateinit var executor: SimpleAsyncTaskExecutor
    private lateinit var environment: ConfigurableEnvironment
    private lateinit var config: TestAsyncSpringLiquibase
    private lateinit var recorder: LogbackRecorder
    private val lock = ReentrantLock()
    private val condition = lock.newCondition()

    @BeforeEach
    fun setup() {
        executor = SimpleAsyncTaskExecutor()
        recorder = LogbackRecorder.forClass(MockEnvironment::class.java).reset().capture("ALL")
        environment = MockEnvironment()
        recorder.release()
        config = Mockito.spy(TestAsyncSpringLiquibase(executor, environment))
        recorder = LogbackRecorder.forClass(AsyncSpringLiquibase::class.java).reset().capture("ALL")
    }

    @AfterEach
    fun teardown() {
        recorder.release()
    }

    @Test
    fun testProfileNoLiquibase() {
        environment.setActiveProfiles(SPRING_PROFILE_NO_LIQUIBASE)
        var caught: Throwable?
        lock.withLock {
            caught = catchThrowable {
                config.afterPropertiesSet()
                condition.await(100, TimeUnit.MILLISECONDS)
            }
            assertThat(caught).isNull()
        }
        caught = catchThrowable {
            verify(config, Mockito.never()).initDb()
        }
        assertThat(caught).isNull()

        val events = recorder.play()
        assertThat(events).hasSize(1)
        val event: Event = events[0]
        assertThat(event.level).isEqualTo("DEBUG")
        assertThat(event.message).isEqualTo(AsyncSpringLiquibase.DISABLED_MESSAGE)
        assertThat(event.thrown).isNull()
    }

    @Test
    fun testProfileProduction() {
        environment.setActiveProfiles(SPRING_PROFILE_PRODUCTION)
        var caught: Throwable?
        lock.withLock {
            caught = catchThrowable {
                config.afterPropertiesSet()
                condition.await(100, TimeUnit.MILLISECONDS)
            }
        }
        caught = catchThrowable {
            verify(config, Mockito.times(1)).initDb()
        }
        assertThat(caught).isNull()

        val events = recorder.play()
        assertThat(events).hasSize(2)
        val event0: Event = events[0]
        assertThat(event0.level).isEqualTo("DEBUG")
        assertThat(event0.message).isEqualTo(AsyncSpringLiquibase.STARTING_SYNC_MESSAGE)
        assertThat(event0.thrown).isNull()
        val event1: Event = events[1]
        assertThat(event1.level).isEqualTo("DEBUG")
        assertThat(event1.message).isEqualTo(AsyncSpringLiquibase.STARTED_MESSAGE)
        assertThat(event1.thrown).isNull()
    }

    @Test
    fun testProfileDevelopment() {
        environment.setActiveProfiles(SPRING_PROFILE_DEVELOPMENT)
        var caught: Throwable?
        lock.withLock {
            caught = catchThrowable {
                config.afterPropertiesSet()
                condition.await(100, TimeUnit.MILLISECONDS)
            }
            assertThat(caught).isNull()
        }
        caught = catchThrowable {
            verify(config, Mockito.times(1)).initDb()
        }
        assertThat(caught).isNull()

        val events = recorder.play()
        assertThat(events).hasSize(2)
        val event0: Event = events[0]
        assertThat(event0.level).isEqualTo("WARN")
        assertThat(event0.message).isEqualTo(AsyncSpringLiquibase.STARTING_ASYNC_MESSAGE)
        assertThat(event0.thrown).isNull()
        val event1: Event = events[1]
        assertThat(event1.level).isEqualTo("DEBUG")
        assertThat(event1.message).isEqualTo(AsyncSpringLiquibase.STARTED_MESSAGE)
        assertThat(event1.thrown).isNull()
    }

    @Test
    fun testProfileHeroku() {
        environment.setActiveProfiles(SPRING_PROFILE_HEROKU)
        var caught: Throwable?
        lock.withLock {
            caught = catchThrowable {
                config.afterPropertiesSet()
                condition.await(100, TimeUnit.MILLISECONDS)
            }
            assertThat(caught).isNull()
        }
        caught = catchThrowable {
            verify(config, Mockito.times(1)).initDb()
        }
        assertThat(caught).isNull()

        val events = recorder.play()
        assertThat(events).hasSize(2)
        val event0: Event = events[0]
        assertThat(event0.level).isEqualTo("WARN")
        assertThat(event0.message).isEqualTo(AsyncSpringLiquibase.STARTING_ASYNC_MESSAGE)
        assertThat(event0.thrown).isNull()
        val event1: Event = events[1]
        assertThat(event1.level).isEqualTo("DEBUG")
        assertThat(event1.message).isEqualTo(AsyncSpringLiquibase.STARTED_MESSAGE)
        assertThat(event1.thrown).isNull()
    }

    @Test
    fun testSlow() {
        environment.setActiveProfiles(SPRING_PROFILE_DEVELOPMENT, SPRING_PROFILE_HEROKU)
        config.sleep = SLOWNESS_THRESHOLD * NUMBER_1000L + 500L
        var caught: Throwable?

        lock.withLock {
            caught = catchThrowable {
                config.afterPropertiesSet()
                condition.await(config.sleep + 500L, TimeUnit.MILLISECONDS)
            }
            assertThat(caught).isNull()
        }

        caught = catchThrowable { verify(config, Mockito.times(1)).initDb() }
        assertThat(caught).isNull()

        val events = recorder.play()
        assertThat(events).hasSize(3)
        val event0: Event = events[0]
        assertThat(event0.level).isEqualTo("WARN")
        assertThat(event0.message).isEqualTo(AsyncSpringLiquibase.STARTING_ASYNC_MESSAGE)
        assertThat(event0.thrown).isNull()
        val event1: Event = events[1]
        assertThat(event1.level).isEqualTo("DEBUG")
        assertThat(event1.message).isEqualTo(AsyncSpringLiquibase.STARTED_MESSAGE)
        assertThat(event1.thrown).isNull()
        val event2 = events[2]
        assertThat(event2.level).isEqualTo("WARN")
        assertThat(event2.message).isEqualTo(AsyncSpringLiquibase.SLOWNESS_MESSAGE)
        assertThat(event2.thrown).isNull()
    }

    @Test
    fun testException() {
        environment.setActiveProfiles(SPRING_PROFILE_DEVELOPMENT, SPRING_PROFILE_HEROKU)
        var caught = catchThrowable { doThrow(exception).`when`(config).initDb() }
        assertThat(caught).isNull()

        lock.withLock {
            caught = catchThrowable {
                config.afterPropertiesSet()
                condition.await(config.sleep + 100L, TimeUnit.MILLISECONDS)
            }
            assertThat(caught).isNull()
        }

        caught = catchThrowable { verify(config, Mockito.times(1)).initDb() }
        assertThat(caught).isNull()

        val events = recorder.play()
        assertThat(events).hasSize(2)
        val event0: Event = events[0]
        assertThat(event0.level).isEqualTo("WARN")
        assertThat(event0.message).isEqualTo(AsyncSpringLiquibase.STARTING_ASYNC_MESSAGE)
        assertThat(event0.thrown).isNull()
        val event1: Event = events[1]
        assertThat(event1.level).isEqualTo("ERROR")
        assertThat(event1.message).isEqualTo(AsyncSpringLiquibase.EXCEPTION_MESSAGE)
        assertThat(event1.thrown).isEqualTo(exception.toString())
    }

    private open class TestAsyncSpringLiquibase(
        executor: TaskExecutor,
        environment: Environment,
        var sleep: Long = 0L
    ) :
        AsyncSpringLiquibase(executor, environment) {

        @Throws(LiquibaseException::class)
        override fun initDb() {
            synchronized(executor) {
                super.initDb()
            }
        }

        // This should never happen
        override fun getDataSource(): DataSource {
            val source = Mockito.mock(DataSource::class.java)
            try {
                doReturn(Mockito.mock(Connection::class.java)).`when`(source).connection
            } catch (x: SQLException) {
                // This should never happen
                throw Error(x)
            }
            return source
        }

        override fun createLiquibase(c: Connection?): Liquibase? {
            return null
        }

        override fun performUpdate(liquibase: Liquibase?) {
            if (sleep > 0) {
                try {
                    Thread.sleep(sleep)
                } catch (x: InterruptedException) {
                    // This should never happen
                    throw Error(x)
                }
            }
        }
    }
}
