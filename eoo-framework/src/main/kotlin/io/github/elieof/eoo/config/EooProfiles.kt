package io.github.elieof.eoo.config

/**
 * Eoo profiles.
 */
interface EooProfiles {
    companion object {

        /** Constant <code>SPRING_PROFILE_DEVELOPMENT="dev"</code> */
        const val SPRING_PROFILE_DEVELOPMENT = "dev"

        /** Constant <code>SPRING_PROFILE_PRODUCTION="prod"</code> */
        const val SPRING_PROFILE_PRODUCTION = "prod"

        /** Spring profile used when deploying with Spring Cloud (used when deploying to CloudFoundry)
        Constant <code>SPRING_PROFILE_CLOUD="cloud"</code> */
        const val SPRING_PROFILE_CLOUD = "cloud"

        /** Spring profile used when deploying to Heroku
        Constant <code>SPRING_PROFILE_HEROKU="heroku"</code> */
        const val SPRING_PROFILE_HEROKU = "heroku"

        /** Constant <code>SPRING_PROFILE_TEST="test"</code> */
        const val SPRING_PROFILE_TEST = "test"

        /** Spring profile used to enable swagger
         * Constant `SPRING_PROFILE_SWAGGER="swagger"`  */
        const val SPRING_PROFILE_SWAGGER = "swagger"

        /** Spring profile used to disable running liquibase
         * Constant `SPRING_PROFILE_NO_LIQUIBASE="no-liquibase"`  */
        const val SPRING_PROFILE_NO_LIQUIBASE = "no-liquibase"
    }
}
