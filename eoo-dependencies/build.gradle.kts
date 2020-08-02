plugins {
    id("java-platform")
}

val projectName = name
val projectDescription = description
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
                description.set(projectDescription)
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
    api(project(":eoo-framework"))
    api(platform(Deps.Spring.Boot.dependencies))
    api(platform(Deps.Logging.logstash))
    api(platform(Deps.cucumberJunit))
    api(platform(Deps.cucumberSpring))
    api(platform(Deps.hazelcastHibernate53))
    api(platform(Deps.hibernateJpaModelGen))
    api(platform(Deps.infinispanBom))
    api(platform(Deps.jjwtApi))
    runtime(platform(Deps.jjwtImpl))
    runtime(platform(Deps.jjwtJackson))
    api(platform(Deps.mapstruct))
    api(platform(Deps.mapstructProcessor))
    api(platform(Deps.metricsCore))
    api(platform(Deps.mongobee))
    api(platform(Deps.mysql))
    api(platform(Deps.problemSpringWeb))
    api(platform(Deps.problemSpringWebflux))
    api(platform(Deps.prometheus))
    api(platform(Deps.reflections))
    api(platform(Deps.springfoxValidators))
    api(platform(Deps.springfoxSwagger))

    api(platform(Deps.Spring.boot))
    api(platform(Deps.Spring.Boot.test))
    api(platform(Deps.Spring.Boot.testAutoconfigure))
    api(platform(Deps.Spring.Boot.actuator))
    api(platform(Deps.Spring.Boot.actuatorAutoconfigure))
    api(platform(Deps.Spring.Boot.autoconfigure))
    api(platform(Deps.Spring.Boot.autoconfigureProcessor))
    api(platform(Deps.Spring.Boot.configurationMetadata))
    api(platform(Deps.Spring.Boot.configurationProcessor))
    api(platform(Deps.Spring.Boot.devtools))
    api(platform(Deps.Spring.Boot.loader))
    api(platform(Deps.Spring.Boot.loaderTools))
    api(platform(Deps.Spring.Boot.propertiesMigrator))
    api(platform(Deps.Spring.Boot.starter))
    api(platform(Deps.Spring.Boot.Starter.actuator))
    api(platform(Deps.Spring.Boot.Starter.aop))
    api(platform(Deps.Spring.Boot.Starter.activemq))
    api(platform(Deps.Spring.Boot.Starter.amqp))
    api(platform(Deps.Spring.Boot.Starter.artemis))
    api(platform(Deps.Spring.Boot.Starter.batch))
    api(platform(Deps.Spring.Boot.Starter.cache))
    api(platform(Deps.Spring.Boot.Starter.cloudConnectors))
    api(platform(Deps.Spring.Boot.Starter.Data.cassandra))
    api(platform(Deps.Spring.Boot.Starter.Data.cassandraReactive))
    api(platform(Deps.Spring.Boot.Starter.Data.couchbaseReactive))
    api(platform(Deps.Spring.Boot.Starter.Data.elasticsearch))
    api(platform(Deps.Spring.Boot.Starter.Data.jest))
    api(platform(Deps.Spring.Boot.Starter.Data.jpa))
    api(platform(Deps.Spring.Boot.Starter.Data.ldap))
    api(platform(Deps.Spring.Boot.Starter.Data.mongodb))
    api(platform(Deps.Spring.Boot.Starter.Data.mongodbReactive))
    api(platform(Deps.Spring.Boot.Starter.Data.redis))
    api(platform(Deps.Spring.Boot.Starter.Data.redisReactive))
    api(platform(Deps.Spring.Boot.Starter.Data.neo4j))
    api(platform(Deps.Spring.Boot.Starter.Data.rest))
    api(platform(Deps.Spring.Boot.Starter.Data.solr))
    api(platform(Deps.Spring.Boot.Starter.freemarker))
    api(platform(Deps.Spring.Boot.Starter.groovyTemplates))
    api(platform(Deps.Spring.Boot.Starter.hateoas))
    api(platform(Deps.Spring.Boot.Starter.integration))
    api(platform(Deps.Spring.Boot.Starter.jdbc))
    api(platform(Deps.Spring.Boot.Starter.jersey))
    api(platform(Deps.Spring.Boot.Starter.jetty))
    api(platform(Deps.Spring.Boot.Starter.jooq))
    api(platform(Deps.Spring.Boot.Starter.json))
    api(platform(Deps.Spring.Boot.Starter.Jta.atomikos))
    api(platform(Deps.Spring.Boot.Starter.Jta.bitronix))
//    api(platform(Deps.Spring.Boot.Starter.Jta.narayana)) // not found for 2.2.8.REKEASE
    api(platform(Deps.Spring.Boot.Starter.log4j2))
    api(platform(Deps.Spring.Boot.Starter.logging))
    api(platform(Deps.Spring.Boot.Starter.mail))
    api(platform(Deps.Spring.Boot.Starter.mustache))
    api(platform(Deps.Spring.Boot.Starter.reactorNetty))
    api(platform(Deps.Spring.Boot.Starter.quartz))
    api(platform(Deps.Spring.Boot.Starter.security))
    api(platform(Deps.Spring.Boot.Starter.test))
    api(platform(Deps.Spring.Boot.Starter.thymeleaf))
    api(platform(Deps.Spring.Boot.Starter.tomcat))
    api(platform(Deps.Spring.Boot.Starter.undertow))
    api(platform(Deps.Spring.Boot.Starter.validation))
    api(platform(Deps.Spring.Boot.Starter.web))
    api(platform(Deps.Spring.Boot.Starter.webflux))
    api(platform(Deps.Spring.Boot.Starter.websocket))
    api(platform(Deps.Spring.Boot.Starter.webServices))

    api(platform(Deps.Spring.Security.jwt))
    api(platform(Deps.Spring.Security.oauth))
    api(platform(Deps.Spring.Security.oauth2))

    api(platform(Deps.Spring.dataBom))
    api(platform(Deps.Spring.cloudBom))
    api(platform(Deps.testcontainersBom))
    api(platform(Deps.infinispanBom))

}
