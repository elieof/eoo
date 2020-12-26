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

    /** Spring profile used when deploying to Amazon ECS
     * Constant `SPRING_PROFILE_AWS_ECS="aws-ecs"`  */
    const val SPRING_PROFILE_AWS_ECS = "aws-ecs"

    /** Spring profile used when deploying to Microsoft Azure
     * Constant `SPRING_PROFILE_AZURE="azure"`  */
    const val SPRING_PROFILE_AZURE = "azure"

    /** Constant `SPRING_PROFILE_TEST="test"` */
    const val SPRING_PROFILE_TEST = "test"

    /** Spring profile used to disable running liquibase
     *
     * Constant `SPRING_PROFILE_NO_LIQUIBASE="no-liquibase"`
     */
    const val SPRING_PROFILE_NO_LIQUIBASE = "no-liquibase"

    /** Spring profile used to enable OpenAPI doc generation
     * Constant `SPRING_PROFILE_API_DOCS="api-docs"`  */
    const val SPRING_PROFILE_API_DOCS = "api-docs"

    /** Spring profile used when deploying to Kubernetes and OpenShift
     * Constant `SPRING_PROFILE_K8S="k8s"`  */
    const val SPRING_PROFILE_K8S = "k8s"
}
