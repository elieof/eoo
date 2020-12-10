import java.io.FileInputStream
import java.util.*

plugins {
    jacoco
    checkstyle

    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("kapt") // Required for annotations processing

    id("java")
//    id("io.spring.dependency-management")

    id("org.springframework.boot")
    id("org.sonarqube")
    id("io.gitlab.arturbosch.detekt")
}
tasks {
    named("install") {
        doLast {
            println(jar.get().archiveFileName.get())
        }
    }
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
}

jacoco {
    toolVersion = "0.8.5"
}

checkstyle {
    toolVersion = "8.34"
    configFile = file("$rootDir/checkstyle.xml")
    tasks.checkstyleTest.get().enabled = true
}

detekt {
    input = files("src/main/kotlin")
    config = files("$rootDir/detekt-config.yml")
    reports {
        xml {
            enabled = true
            destination = file("$buildDir/reports/detekt/detekt.xml")
        }
    }
}

kapt {
    arguments {
        arg(
            "org.springframework.boot.configurationprocessor.additionalMetadataLocations",
            "$projectDir/src/main/resources"
        )
    }
}

tasks.test {
    useJUnitPlatform()

    testLogging {
        events("FAILED", "SKIPPED")
    }
    reports.html.isEnabled = false
}

tasks.jacocoTestReport {

    reports {
        xml.isEnabled = true
    }
}

tasks.publishToMavenLocal {
    dependsOn(tasks.test)
}
tasks.check {
    dependsOn(tasks.jacocoTestReport)
}

tasks.register<TestReport>("testReport") {
    destinationDir = file("$buildDir/reports/tests")
    reportOn(tasks.test)
}

val sonarFile = FileInputStream("$rootDir/sonar-project.properties")
val sonarProperties = Properties()
sonarProperties.load(sonarFile)

sonarProperties.forEach { (key, value) ->
    sonarqube {
        properties {
            property(key as String, value)
        }
    }
}

tasks.dokkaJavadoc {
    outputDirectory.set(buildDir.resolve("$buildDir/dokka"))
}

val dokkaJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles Kotlin docs with Dokka"
    archiveClassifier.set("javadoc")
    from(tasks.dokkaJavadoc)
}

val sourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
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
            from(components["java"])
            artifact(dokkaJar)
            artifact(sourcesJar)
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
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

java {
    val sources = sourceSets.main.get()
    registerFeature("logging") {
        usingSourceSet(sources)
    }
    registerFeature("jpa") {
        usingSourceSet(sources)
    }
    registerFeature("cloud") {
        usingSourceSet(sources)
    }
    registerFeature("mail") {
        usingSourceSet(sources)
    }
    registerFeature("security") {
        usingSourceSet(sources)
    }
}

dependencies {
    implementation(platform(project(":eoo-dependencies")))
//    implementation(platform(Eoo.Deps.Spring.Boot.dependencies))
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")

    kapt(Eoo.Deps.Spring.Boot.configurationProcessor)
//    kapt("org.springframework.boot:spring-boot-configuration-processor")
    kapt(Eoo.Deps.hibernateJpaModelGen)

    implementation("org.springframework.boot:spring-boot-autoconfigure")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework:spring-context-support")
    implementation("io.springfox:springfox-oas")
    implementation("io.springfox:springfox-swagger2")
    implementation("io.springfox:springfox-bean-validators")

//    implementation("com.fasterxml.jackson.core:jackson-databind:2.11.1")
//    implementation("com.fasterxml.jackson.core:jackson-annotations:2.11.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    testImplementation("com.h2database:h2")
    testImplementation(Eoo.Deps.mockk)
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude("org.junit.vintage", "junit-vintage-engine")
    }
    testImplementation("org.springframework.security:spring-security-test")

    "loggingImplementation"("org.springframework.boot:spring-boot-starter-logging")
    "loggingImplementation"("com.fasterxml.jackson.module:jackson-module-jaxb-annotations:2.11.1")
    "loggingImplementation"(Eoo.Deps.Logging.logstash)
    "loggingImplementation"(Eoo.Deps.Logging.kotlin)
    "loggingImplementation"(Eoo.Deps.Logging.LogBook.spring)
    "loggingImplementation"(Eoo.Deps.Logging.LogBook.logstash)
//    "loggingImplementation"(Eoo.Deps.Logging.LogBook.json)

    "mailImplementation"("org.springframework.boot:spring-boot-starter-mail")

    "jpaImplementation"("org.liquibase:liquibase-core")
    "jpaImplementation"("com.fasterxml.jackson.datatype:jackson-datatype-hibernate5")
    "jpaImplementation"("org.springframework.boot:spring-boot-starter-data-jpa")
    "jpaImplementation"("javax.cache:cache-api")
    "jpaImplementation"("org.hibernate:hibernate-jcache")

    "cloudImplementation"(Eoo.Deps.Spring.cloudNetflix) {
        exclude("org.hdrhistogram", "HdrHistogram")
    }
    "cloudImplementation"("org.springframework.boot:spring-boot-actuator-autoconfigure")
    "cloudImplementation"("org.springframework.boot:spring-boot-actuator")
    "cloudImplementation"("io.dropwizard.metrics:metrics-core")
    "cloudImplementation"("io.micrometer:micrometer-core")

    "securityImplementation"("org.springframework.boot:spring-boot-starter-security")
    "securityImplementation"("org.springframework.boot:spring-boot-starter-undertow")
    "securityImplementation"("org.springframework.security:spring-security-data")
    "securityImplementation"(Eoo.Deps.Spring.Security.oauth2)
}

configurations.all {
    resolutionStrategy {
        eachDependency {
            // Force Kotlin to our version
            if (requested.group == "org.jetbrains.kotlin") {
                useVersion(Eoo.kotlinVersion)
            }
        }
    }
}
