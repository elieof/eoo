package io.github.elieof.eoo.config

/**
 * Eoo profiles.
 */
object EooProfiles {

    /** Constant `SPRING_PROFILE_DEVELOPMENT="dev"` */
    const val SPRING_PROFILE_DEVELOPMENT = "dev"

    /** Constant `SPRING_PROFILE_PRODUCTION="prod"` */
    const val SPRING_PROFILE_PRODUCTION = "prod"

    /** Spring profile used when deploying with Spring Cloud (used when deploying to CloudFoundry)
    Constant `SPRING_PROFILE_CLOUD="cloud"` */
    const val SPRING_PROFILE_CLOUD = "cloud"

    /** Spring profile used when deploying to Heroku
    Constant `SPRING_PROFILE_HEROKU="heroku"` */
    const val SPRING_PROFILE_HEROKU = "heroku"

    /** Constant `SPRING_PROFILE_TEST="test"` */
    const val SPRING_PROFILE_TEST = "test"

    /** Spring profile used to enable swagger
     *
     * Constant `SPRING_PROFILE_SWAGGER="swagger"`
     */
    const val SPRING_PROFILE_SWAGGER = "swagger"

    /** Spring profile used to disable running liquibase
     *
     * Constant `SPRING_PROFILE_NO_LIQUIBASE="no-liquibase"`
     */
    const val SPRING_PROFILE_NO_LIQUIBASE = "no-liquibase"
}
