@file:Suppress("MemberVisibilityCanBePrivate")

object Eoo {
    const val kotlinVersion = "1.4.21"
    const val coroutinesVersion = "1.4.2"
    const val gradleVersion = "6.7.1"

    const val springBootVersion = "2.3.7.RELEASE"
    const val springDependenciesManagementVersion = "1.0.10.RELEASE"

    const val ktlintVersion = "0.40.0"
    const val ktlintGVersion = "9.4.0"
    const val detektVersion = "1.15.0-RC1"
    const val sonarqubeVersion = "3.0"
    const val nohttpVersion = "0.0.5.RELEASE"
    const val dokkaVersion = "1.4.20"

    const val awsJavaVersion = "1.11.922"
    const val azureSpringCloudClientVersion = "2.3.0"
    const val azureSpringBootVersion = "2.6.2"
    const val bucket4jVersion = "4.10.0"
    const val cassandraUnitVersion = "4.3.1.0"
    const val couchmoveVersion = "3.1"
    const val cucumberJvmVersion = "6.9.1"
    const val commonsIoVersion = "2.8.0"

    /**
     * The hibernate version should match the one managed by
     * [springBoot](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-dependencies/2.2.3.RELEASE)
     */
    const val deFlapDoodleVersion = "2.2.0"
    const val hazelcastVersion = "4.1"
    const val hazelcastHibernate53Version = "2.1.1"
    const val hibernateVersion = "5.4.23.Final"
    const val infinispanVersion = "10.1.8.Final"
    const val infinispanSpringBootVersion = "2.2.4.Final"
    const val kotlinLoggingVersion = "2.0.3"
    const val jjwtVersion = "0.11.2"
    const val liquibaseMssqlVersion = "1.6.4"
    const val logstashLogbackEncoderVersion = "6.5"
    const val lz4Version = "1.7.1"
    const val logbookVersion = "2.1.4"
    const val mapstructVersion = "1.4.1.Final"
    const val micrometerVersion = "1.4.2"
    const val mongockVersion = "4.1.19"
    const val mongobeeVersion = "0.13"
    const val mockito4kotlinVersion = "0.4.1"
    const val mockitokotlin2Version = "2.2.0"
    const val mockkVersion = "1.10.3-jdk8"
    const val mssqlJdbcVersion = "8.4.1.jre8"
    const val mysqlVersion = "8.0.22"
    const val neo4jVersion = "0.0.13"
    const val oracleVersion = "19.8.0.0"
    const val problemSpringVersion = "0.26.2"
    const val prometheusVersion = "0.9.0"
    const val redissonVersion = "3.14.0"
    const val reflectionsVersion = "0.9.11"
    const val springDataReleaseTrainVersion = "Moore-SR7"
    const val springCloudVersion = "Hoxton.SR9"
    const val springCloudNetflixVersion = "2.2.3.RELEASE"

    //    const val springDataJestVersion = "3.3.1.RELEASE"
    const val springDataNeo4jVersion = "1.1.1"
    const val springSecurityJwtVersion = "1.1.1.RELEASE"
    const val springSecurityOauthVersion = "2.5.0.RELEASE"
    const val springfoxVersion = "3.0.0"
    const val testcontainersVersion = "1.15.1"
    const val xmemcachedVersion = "2.4.6"
    const val xmemcachedProviderVersion = "4.1.3"

    object Deps {

        const val awsBom = "com.amazonaws:aws-java-sdk-bom:$awsJavaVersion"
        const val azureSpringCloudClient =
            "com.microsoft.azure:spring-cloud-starter-azure-spring-cloud-client:$azureSpringCloudClientVersion"
        const val azureSpringBoot =
            "com.microsoft.azure:applicationinsights-spring-boot-starter:$azureSpringBootVersion"
        const val bucket4jCore = "com.github.vladimir-bukhtoyarov:bucket4j-core:$bucket4jVersion"
        const val bucket4jCache = "com.github.vladimir-bukhtoyarov:bucket4j-jcache:$bucket4jVersion"
        const val cassandraUnitSpring = "org.cassandraunit:cassandra-unit-spring:$cassandraUnitVersion"
        const val commonsIo = "commons-io:commons-io:$commonsIoVersion"
        const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion"
        const val couchmove = "com.github.differentway:couchmove:$couchmoveVersion"
        const val cucumberJava = "io.cucumber:cucumber-java:$cucumberJvmVersion"
        const val cucumberJunit = "io.cucumber:cucumber-junit:$cucumberJvmVersion"
        const val cucumberSpring = "io.cucumber:cucumber-spring:$cucumberJvmVersion"
        const val deFlapDoodle = "de.flapdoodle.embed:de.flapdoodle.embed.mongo:$deFlapDoodleVersion"
        const val hazelcast = "com.hazelcast:hazelcast:$hazelcastVersion"
        const val hazelcastSpring = "com.hazelcast:hazelcast-spring:$hazelcastVersion"
        const val hazelcastHibernate53 = "com.hazelcast:hazelcast-hibernate53:$hazelcastHibernate53Version"
        const val hibernateJpaModelGen = "org.hibernate:hibernate-jpamodelgen:$hibernateVersion"
        const val infinispanBom = "org.infinispan:infinispan-bom:$infinispanVersion"
        const val infinispanSpringBoot =
            "org.infinispan:infinispan-spring-boot-starter-embedded:$infinispanSpringBootVersion"
        const val jjwtApi = "io.jsonwebtoken:jjwt-api:$jjwtVersion"
        const val jjwtImpl = "io.jsonwebtoken:jjwt-impl:$jjwtVersion"
        const val jjwtJackson = "io.jsonwebtoken:jjwt-jackson:$jjwtVersion"
        const val liquibaseMssql = "com.github.sabomichal:liquibase-mssql:$liquibaseMssqlVersion"
        const val lz4Java = "org.lz4:lz4-java:$lz4Version"
        const val mapstruct = "org.mapstruct:mapstruct:$mapstructVersion"
        const val mapstructProcessor = "org.mapstruct:mapstruct-processor:$mapstructVersion"
        const val micrometer = "io.micrometer:micrometer-core:$micrometerVersion"
        const val mongobee = "com.github.mongobee:mongobee:$mongobeeVersion"
        const val mongockBom = "com.github.cloudyrock.mongock:mongock-bom:$mongockVersion"
        const val mockito4kotlin = "org.mockito4kotlin:annotation:$mockito4kotlinVersion"
        const val mockitokotlin2 = "com.nhaarman.mockitokotlin2:mockito-kotlin:$mockitokotlin2Version"
        const val mockk = "io.mockk:mockk:$mockkVersion"
        const val mysql = "mysql:mysql-connector-java:$mysqlVersion"
        const val mssql = "com.microsoft.sqlserver:mssql-jdbc:$mssqlJdbcVersion"
        const val neo4jMigrations = "eu.michael-simons.neo4j:neo4j-migrations-spring-boot-starter:$neo4jVersion"
        const val oracle = "com.oracle.database.jdbc:ojdbc8:$oracleVersion"
        const val problemSpringWeb = "org.zalando:problem-spring-web:$problemSpringVersion"
        const val problemSpringWebflux = "org.zalando:problem-spring-webflux:$problemSpringVersion"
        const val prometheus = "io.prometheus:simpleclient:$prometheusVersion"
        const val redisson = "org.redisson:redisson:$redissonVersion"
        const val reflections = "org.reflections:reflections:$reflectionsVersion"
        const val springfoxValidators = "io.springfox:springfox-bean-validators:$springfoxVersion"
        const val springfoxSwagger = "io.springfox:springfox-swagger2:$springfoxVersion"
        const val springfoxOas = "io.springfox:springfox-oas:$springfoxVersion"
        const val testcontainersBom = "org.testcontainers:testcontainers-bom:$testcontainersVersion"
        const val xmemcached = "com.googlecode.xmemcached:xmemcached:$xmemcachedVersion"
        const val xmemcachedSpring = "com.google.code.simple-spring-memcached:spring-cache:$xmemcachedProviderVersion"
        const val xmemcachedProvider =
            "com.google.code.simple-spring-memcached:xmemcached-provider:$xmemcachedProviderVersion"

        object Logging {
            const val logstash = "net.logstash.logback:logstash-logback-encoder:$logstashLogbackEncoderVersion"
            const val kotlin = "io.github.microutils:kotlin-logging-jvm:$kotlinLoggingVersion"
            object LogBook {
                private const val prefix = "org.zalando"

                const val core = "$prefix:logbook-core:$logbookVersion"
                const val spring = "$prefix:logbook-spring-boot-starter:$logbookVersion"
                const val logstash = "$prefix:logbook-logstash:$logbookVersion"
                const val json = "$prefix:logbook-json:$logbookVersion"
            }
        }

        object Spring {
            private const val prefix = "org.springframework"

            const val boot = "$prefix.boot:spring-boot:$springBootVersion"
            const val dataBom = "$prefix.data:spring-data-releasetrain:$springDataReleaseTrainVersion"
            const val cloudBom = "$prefix.cloud:spring-cloud-dependencies:$springCloudVersion"
            const val cloudNetflix = "$prefix.cloud:spring-cloud-starter-netflix-ribbon:$springCloudNetflixVersion"

            object Boot {
                private const val prefix = "org.springframework.boot:spring-boot"

                const val test = "$prefix-test:$springBootVersion"
                const val testAutoconfigure = "$prefix-test-autoconfigure:$springBootVersion"
                const val actuator = "$prefix-actuator:$springBootVersion"
                const val actuatorAutoconfigure = "$prefix-actuator-autoconfigure:$springBootVersion"
                const val autoconfigure = "$prefix-autoconfigure:$springBootVersion"
                const val autoconfigureProcessor = "$prefix-autoconfigure-processor:$springBootVersion"
                const val configurationMetadata = "$prefix-configuration-metadata:$springBootVersion"
                const val configurationProcessor = "$prefix-configuration-processor:$springBootVersion"
                const val dependencies = "$prefix-dependencies:$springBootVersion"
                const val devtools = "$prefix-devtools:$springBootVersion"
                const val loader = "$prefix-loader:$springBootVersion"
                const val loaderTools = "$prefix-loader-tools:$springBootVersion"
                const val propertiesMigrator = "$prefix-properties-migrator:$springBootVersion"
                const val starter = "$prefix-starter:$springBootVersion"

                object Starter {
                    private const val prefix = "org.springframework.boot:spring-boot-starter"

                    const val actuator = "$prefix-actuator:$springBootVersion"
                    const val aop = "$prefix-aop:$springBootVersion"
                    const val activemq = "$prefix-activemq:$springBootVersion"
                    const val amqp = "$prefix-amqp:$springBootVersion"
                    const val artemis = "$prefix-artemis:$springBootVersion"
                    const val batch = "$prefix-batch:$springBootVersion"
                    const val cache = "$prefix-cache:$springBootVersion"
                    // const val cloudConnectors = "$prefix-cloud-connectors:$springBootVersion"// not supported in version > 2.3

                    object Data {
                        const val cassandra = "$prefix-data-cassandra:$springBootVersion"
                        const val cassandraReactive = "$prefix-data-cassandra-reactive:$springBootVersion"
                        const val couchbase = "$prefix-data-couchbase:$springBootVersion"
                        const val couchbaseReactive = "$prefix-data-couchbase-reactive:$springBootVersion"
                        const val elasticsearch = "$prefix-data-elasticsearch:$springBootVersion"

                        //                        const val jest = "com.github.vanroy:spring-boot-starter-data-jest:$springDataJestVersion"
                        const val jpa = "$prefix-data-jpa:$springBootVersion"
                        const val ldap = "$prefix-data-ldap:$springBootVersion"
                        const val mongodb = "$prefix-data-mongodb:$springBootVersion"
                        const val mongodbReactive = "$prefix-data-mongodb-reactive:$springBootVersion"
                        const val redis = "$prefix-data-redis:$springBootVersion"
                        const val redisReactive = "$prefix-data-redis-reactive:$springBootVersion"
                        const val neo4j = "$prefix-data-neo4j:$springBootVersion"
                        const val rest = "$prefix-data-rest:$springBootVersion"
                        const val solr = "$prefix-data-solr:$springBootVersion"
                    }

                    const val freemarker = "$prefix-freemarker:$springBootVersion"
                    const val groovyTemplates = "$prefix-groovy-templates:$springBootVersion"
                    const val hateoas = "$prefix-hateoas:$springBootVersion"
                    const val integration = "$prefix-integration:$springBootVersion"
                    const val jdbc = "$prefix-jdbc:$springBootVersion"
                    const val jersey = "$prefix-jersey:$springBootVersion"
                    const val jetty = "$prefix-jetty:$springBootVersion"
                    const val jooq = "$prefix-jooq:$springBootVersion"
                    const val json = "$prefix-json:$springBootVersion"

                    object Jta {
                        const val atomikos = "$prefix-jta-atomikos:$springBootVersion"
                        const val bitronix = "$prefix-jta-bitronix:$springBootVersion"
//                    const val narayana = "$prefix-jta-narayana:$springBootVersion"
                    }

                    const val log4j2 = "$prefix-log4j2:$springBootVersion"
                    const val logging = "$prefix-logging:$springBootVersion"
                    const val mail = "$prefix-mail:$springBootVersion"
                    const val mustache = "$prefix-mustache:$springBootVersion"
                    const val reactorNetty = "$prefix-reactor-netty:$springBootVersion"
                    const val quartz = "$prefix-quartz:$springBootVersion"
                    const val security = "$prefix-security:$springBootVersion"
                    const val test = "$prefix-test:$springBootVersion"
                    const val thymeleaf = "$prefix-thymeleaf:$springBootVersion"
                    const val tomcat = "$prefix-tomcat:$springBootVersion"
                    const val undertow = "$prefix-undertow:$springBootVersion"
                    const val validation = "$prefix-validation:$springBootVersion"
                    const val web = "$prefix-web:$springBootVersion"
                    const val webflux = "$prefix-webflux:$springBootVersion"
                    const val websocket = "$prefix-websocket:$springBootVersion"
                    const val webServices = "$prefix-web-services:$springBootVersion"
                }
            }

            object Security {
                private const val prefix = "org.springframework.security"

                const val jwt = "$prefix:spring-security-jwt:$springSecurityJwtVersion"
                const val oauth = "$prefix.oauth:spring-security-oauth:$springSecurityOauthVersion"
                const val oauth2 = "$prefix.oauth:spring-security-oauth2:$springSecurityOauthVersion"
            }
        }
    }
}
