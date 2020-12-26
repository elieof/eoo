package io.github.elieof.eoo.config.liquibase

import io.github.elieof.eoo.config.EooProfiles.SPRING_PROFILE_DEVELOPMENT
import io.github.elieof.eoo.config.EooProfiles.SPRING_PROFILE_HEROKU
import io.github.elieof.eoo.config.EooProfiles.SPRING_PROFILE_NO_LIQUIBASE
import liquibase.exception.LiquibaseException
import mu.KotlinLogging
import org.springframework.boot.autoconfigure.liquibase.DataSourceClosingSpringLiquibase
import org.springframework.core.env.Environment
import org.springframework.core.env.Profiles
import org.springframework.util.StopWatch
import java.sql.SQLException
import java.util.concurrent.Executor

private val logger = KotlinLogging.logger {}

/**
 * Start Liquibase asynchronously When using profile [SPRING_PROFILE_DEVELOPMENT]
 */
open class AsyncSpringLiquibase(
    private val executor: Executor,
    private val env: Environment
) : DataSourceClosingSpringLiquibase() {

    companion object {
        /** Constant `DISABLED_MESSAGE="Liquibase is disabled"`  */
        const val DISABLED_MESSAGE = "Liquibase is disabled"

        /** Constant `STARTING_ASYNC_MESSAGE="Starting Liquibase asynchronously, your"{trunked}`  */
        const val STARTING_ASYNC_MESSAGE =
            "Starting Liquibase asynchronously, your database might not be ready at startup!"

        /** Constant `STARTING_SYNC_MESSAGE="Starting Liquibase synchronously"`  */
        const val STARTING_SYNC_MESSAGE = "Starting Liquibase synchronously"

        /** Constant `STARTED_MESSAGE="Liquibase has updated your database in "{trunked}`  */
        const val STARTED_MESSAGE = "Liquibase has updated your database in {} ms"

        /** Constant `EXCEPTION_MESSAGE="Liquibase could not start correctly, yo"{trunked}`  */
        const val EXCEPTION_MESSAGE = "Liquibase could not start correctly, your database is NOT ready: {}"

        /** Constant `SLOWNESS_THRESHOLD=5`  */
        const val SLOWNESS_THRESHOLD: Long = 5 // seconds

        /** Constant `NUMBER_1000L=1000L`  */
        const val NUMBER_1000L: Long = 1000L

        /** Constant `SLOWNESS_MESSAGE="Warning, Liquibase took more than {} se"{trunked}`  */
        const val SLOWNESS_MESSAGE = "Warning, Liquibase took more than {} seconds to start up!"
    }

    override fun afterPropertiesSet() {
        if (!env.acceptsProfiles(Profiles.of(SPRING_PROFILE_NO_LIQUIBASE))) {
            if (env.acceptsProfiles(Profiles.of("$SPRING_PROFILE_DEVELOPMENT|$SPRING_PROFILE_HEROKU"))) {
                connect()
            } else {
                logger.debug(STARTING_SYNC_MESSAGE)
                initDb()
            }
        } else {
            logger.debug(DISABLED_MESSAGE)
        }
    }

    private fun connect() {
        // Prevent Thread Lock with spring-cloud-context GenericScope
        // https://github.com/spring-cloud/spring-cloud-commons/commit/aaa7288bae3bb4d6fdbef1041691223238d77b7b#diff-afa0715eafc2b0154475fe672dab70e4R328
        return try {
            getDataSource().connection.use {
                executor.execute {
                    try {
                        logger.warn(STARTING_ASYNC_MESSAGE)
                        initDb()
                    } catch (e: LiquibaseException) {
                        logger.error(EXCEPTION_MESSAGE, e.message, e)
                    }
                }
            }
        } catch (e: SQLException) {
            logger.error(EXCEPTION_MESSAGE, e.message, e)
        }
    }

    /**
     *
     * initDb.
     *
     * @throws liquibase.exception.LiquibaseException if any.
     */
    @Throws(LiquibaseException::class)
    open fun initDb() {
        val watch = StopWatch()
        watch.start()
        super.afterPropertiesSet()
        watch.stop()
        logger.debug(STARTED_MESSAGE, watch.totalTimeMillis)
        if (watch.totalTimeMillis > SLOWNESS_THRESHOLD * NUMBER_1000L) {
            logger.warn(SLOWNESS_MESSAGE, SLOWNESS_THRESHOLD)
        }
    }
}
