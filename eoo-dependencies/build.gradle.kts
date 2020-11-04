plugins {
    id("java-platform")
}

val projectName = name
val repoUrl = "https://github.com/elieof/eoo"
val signingKey: String? = project.findProperty("signingKey") as String?
    ?: System.getenv("SIGNING_KEY")?.replace("\\n", System.lineSeparator())
val signingPassword: String? = project.findProperty("signingPassword") as String? ?: System.getenv("SIGNING_PASSWORD")

configure<PublishingExtension> {
    publications {
        val mavenPublication = register<MavenPublication>(projectName) {
            from(components["javaPlatform"])
            pom {
                name.set(projectName)
                description.set("Eoo framework BOM")
                url.set(repoUrl)
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("elieof")
                        name.set("Olivier Ouemba")
                        email.set("olivier.ouemba@hotmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:$repoUrl")
                    developerConnection.set("scm:git:$repoUrl")
                    url.set(repoUrl)
                }
                issueManagement {
                    url.set("https://hackerone.com/central-security-project/reports/new")
                }
            }
        }
        signing {
            useInMemoryPgpKeys(signingKey, signingPassword)
            sign(mavenPublication.get())
        }
    }
}

javaPlatform {
    allowDependencies()
}

dependencies {
    api(platform(Eoo.Deps.Spring.Boot.dependencies))
    api(platform(Eoo.Deps.awsBom))
    api(platform(Eoo.Deps.Spring.dataBom))
    api(platform(Eoo.Deps.Spring.cloudBom))
    api(platform(Eoo.Deps.testcontainersBom))
    api(platform(Eoo.Deps.mongockBom))
    api(platform(Eoo.Deps.infinispanBom))

    constraints {
        api(project(":eoo-framework"))
        api(Eoo.Deps.azureSpringBoot)
        api(Eoo.Deps.azureSpringCloudClient)
        api(Eoo.Deps.bucket4jCore)
        api(Eoo.Deps.bucket4jCache)
        api(Eoo.Deps.cassandraUnitSpring)
        api(Eoo.Deps.commonsIo)
        api(Eoo.Deps.couchmove)
        api(Eoo.Deps.cucumberJava)
        api(Eoo.Deps.cucumberJunit)
        api(Eoo.Deps.cucumberSpring)
        api(Eoo.Deps.deFlapDoodle)
        api(Eoo.Deps.guava)
        api(Eoo.Deps.xmemcached)
        api(Eoo.Deps.xmemcachedProvider)
        api(Eoo.Deps.xmemcachedSpring)
        api(Eoo.Deps.redisson)
        api(Eoo.Deps.infinispanSpringBoot)
        api(Eoo.Deps.hazelcast)
        api(Eoo.Deps.hazelcastSpring)
        api(Eoo.Deps.hazelcastHibernate53)
        api(Eoo.Deps.hibernateJpaModelGen)
        api(Eoo.Deps.infinispanBom)
        api(Eoo.Deps.jjwtApi)
        runtime(Eoo.Deps.jjwtImpl)
        runtime(Eoo.Deps.jjwtJackson)
        api(Eoo.Deps.liquibaseMssql)
        api(Eoo.Deps.Logging.logstash)
        api(Eoo.Deps.Logging.kotlin)
        api(Eoo.Deps.lz4Java)
        api(Eoo.Deps.mapstruct)
        api(Eoo.Deps.mapstructProcessor)
        api(Eoo.Deps.metricsCore)
        api(Eoo.Deps.mongobee)
        api(Eoo.Deps.mysql)
        api(Eoo.Deps.mssql)
        api(Eoo.Deps.neo4jMigrations)
        api(Eoo.Deps.oracle)
        api(Eoo.Deps.problemSpringWeb)
        api(Eoo.Deps.problemSpringWebflux)
        api(Eoo.Deps.prometheus)
        api(Eoo.Deps.reflections)
        api(Eoo.Deps.springfoxValidators)
        api(Eoo.Deps.springfoxSwagger)
        api(Eoo.Deps.springfoxOas)

        api(Eoo.Deps.Spring.boot)
        api(Eoo.Deps.Spring.Boot.test)
        api(Eoo.Deps.Spring.Boot.testAutoconfigure)
        api(Eoo.Deps.Spring.Boot.actuator)
        api(Eoo.Deps.Spring.Boot.actuatorAutoconfigure)
        api(Eoo.Deps.Spring.Boot.autoconfigure)
        api(Eoo.Deps.Spring.Boot.autoconfigureProcessor)
        api(Eoo.Deps.Spring.Boot.configurationMetadata)
        api(Eoo.Deps.Spring.Boot.configurationProcessor)
        api(Eoo.Deps.Spring.Boot.devtools)
        api(Eoo.Deps.Spring.Boot.loader)
        api(Eoo.Deps.Spring.Boot.loaderTools)
        api(Eoo.Deps.Spring.Boot.propertiesMigrator)
        api(Eoo.Deps.Spring.Boot.starter)
        api(Eoo.Deps.Spring.Boot.Starter.actuator)
        api(Eoo.Deps.Spring.Boot.Starter.aop)
        api(Eoo.Deps.Spring.Boot.Starter.activemq)
        api(Eoo.Deps.Spring.Boot.Starter.amqp)
        api(Eoo.Deps.Spring.Boot.Starter.artemis)
        api(Eoo.Deps.Spring.Boot.Starter.batch)
        api(Eoo.Deps.Spring.Boot.Starter.cache)
        api(Eoo.Deps.Spring.Cloud.service)
        api(Eoo.Deps.Spring.Cloud.heroku)
        api(Eoo.Deps.Spring.Cloud.localConfig)
        api(Eoo.Deps.Spring.Boot.Starter.Data.cassandra)
        api(Eoo.Deps.Spring.Boot.Starter.Data.cassandraReactive)
        api(Eoo.Deps.Spring.Boot.Starter.Data.couchbaseReactive)
        api(Eoo.Deps.Spring.Boot.Starter.Data.elasticsearch)
//    api(Eoo.Deps.Spring.Boot.Starter.Data.jest)
        api(Eoo.Deps.Spring.Boot.Starter.Data.jpa)
        api(Eoo.Deps.Spring.Boot.Starter.Data.ldap)
        api(Eoo.Deps.Spring.Boot.Starter.Data.mongodb)
        api(Eoo.Deps.Spring.Boot.Starter.Data.mongodbReactive)
        api(Eoo.Deps.Spring.Boot.Starter.Data.redis)
        api(Eoo.Deps.Spring.Boot.Starter.Data.redisReactive)
        api(Eoo.Deps.Spring.Boot.Starter.Data.neo4j)
        api(Eoo.Deps.Spring.Boot.Starter.Data.rest)
        api(Eoo.Deps.Spring.Boot.Starter.Data.solr)
        api(Eoo.Deps.Spring.Boot.Starter.freemarker)
        api(Eoo.Deps.Spring.Boot.Starter.groovyTemplates)
        api(Eoo.Deps.Spring.Boot.Starter.hateoas)
        api(Eoo.Deps.Spring.Boot.Starter.integration)
        api(Eoo.Deps.Spring.Boot.Starter.jdbc)
        api(Eoo.Deps.Spring.Boot.Starter.jersey)
        api(Eoo.Deps.Spring.Boot.Starter.jetty)
        api(Eoo.Deps.Spring.Boot.Starter.jooq)
        api(Eoo.Deps.Spring.Boot.Starter.json)
        api(Eoo.Deps.Spring.Boot.Starter.Jta.atomikos)
        api(Eoo.Deps.Spring.Boot.Starter.Jta.bitronix)
//    api(Eoo.Deps.Spring.Boot.Starter.Jta.narayana) // not found for 2.2.8.REKEASE
        api(Eoo.Deps.Spring.Boot.Starter.log4j2)
        api(Eoo.Deps.Spring.Boot.Starter.logging)
        api(Eoo.Deps.Spring.Boot.Starter.mail)
        api(Eoo.Deps.Spring.Boot.Starter.mustache)
        api(Eoo.Deps.Spring.Boot.Starter.reactorNetty)
        api(Eoo.Deps.Spring.Boot.Starter.quartz)
        api(Eoo.Deps.Spring.Boot.Starter.security)
        api(Eoo.Deps.Spring.Boot.Starter.test)
        api(Eoo.Deps.Spring.Boot.Starter.thymeleaf)
        api(Eoo.Deps.Spring.Boot.Starter.tomcat)
        api(Eoo.Deps.Spring.Boot.Starter.undertow)
        api(Eoo.Deps.Spring.Boot.Starter.validation)
        api(Eoo.Deps.Spring.Boot.Starter.web)
        api(Eoo.Deps.Spring.Boot.Starter.webflux)
        api(Eoo.Deps.Spring.Boot.Starter.websocket)
        api(Eoo.Deps.Spring.Boot.Starter.webServices)

        api(Eoo.Deps.Spring.Security.jwt)
        api(Eoo.Deps.Spring.Security.oauth)
        api(Eoo.Deps.Spring.Security.oauth2)
    }
}
