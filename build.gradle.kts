import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        mavenLocal()
        mavenCentral()
//        jcenter()
        gradlePluginPortal()
        maven { url = uri("https://repo.spring.io/plugins-release") }
    }
}

plugins {
    kotlin("jvm") version Eoo.kotlinVersion
    kotlin("plugin.spring") version Eoo.kotlinVersion apply false
    kotlin("kapt") version Eoo.kotlinVersion apply false

    id("org.springframework.boot") version Eoo.springBootVersion apply false
    id("io.spring.dependency-management") version Eoo.springDependenciesManagementVersion apply false
    id("org.jlleitschuh.gradle.ktlint") version Eoo.ktlintGVersion
    id("io.gitlab.arturbosch.detekt") version Eoo.detektVersion
    id("org.sonarqube") version Eoo.sonarqubeVersion apply false
    id("io.spring.nohttp") version Eoo.nohttpVersion
    id("org.jetbrains.dokka") version Eoo.dokkaVersion apply false
    signing
}

tasks.named<Wrapper>("wrapper") {
    gradleVersion = Eoo.gradleVersion
    distributionType = Wrapper.DistributionType.ALL
}

allprojects {
    group = "io.github.elieof.eoo"
    version = "0.0.1-SNAPSHOT"
}

repositories {
    mavenLocal()
    mavenCentral()
//    jcenter()
    gradlePluginPortal()
    maven { url = uri("https://repo.spring.io/plugins-release") }
}

// val signingKeyId: String? = project.findProperty("signingKeyId") as String? ?: System.getenv("SIGNING_KEY_ID")

subprojects {

    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://repo.spring.io/plugins-release") }
    }

    apply {
        plugin("idea")
//        plugin("java")
//        plugin("maven")
        plugin("maven-publish")
//        plugin("io.spring.dependency-management")
        plugin("io.spring.nohttp")
        plugin("org.jlleitschuh.gradle.ktlint")
        plugin("org.jlleitschuh.gradle.ktlint-idea")
        plugin("org.jetbrains.dokka")
        plugin("signing")
    }

    tasks.withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }

    configure<PublishingExtension> {
        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/elieof/eoo")
                credentials {
                    username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                    password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
                }
            }
            maven {
                name = "OSSRH"
                val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
                val snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots")
                url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
                credentials {
                    username = project.findProperty("ossrh.user") as String? ?: System.getenv("MAVEN_USERNAME")
                    password = project.findProperty("ossrh.password") as String? ?: System.getenv("MAVEN_PASSWORD")
                }
            }
        }
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict", "-Xjvm-default=all")
            jvmTarget = "11"
            apiVersion = "1.4"
            languageVersion = "1.4"
        }
        dependsOn(tasks.ktlintFormat)
    }

    ktlint {
        ignoreFailures.set(true)
        version.set(Eoo.ktlintVersion)
        disabledRules.set(setOf("import-ordering", "no-wildcard-imports"))
    }
}

nohttp {
    source.include("build.gradle", "README.md")
}

ktlint {
    ignoreFailures.set(true)
    version.set(Eoo.ktlintVersion)
    disabledRules.set(setOf("import-ordering", "no-wildcard-imports"))
}

configurations.all {
    resolutionStrategy {
        eachDependency {
            // Force Kotlin to our version
            if (requested.group == "org.jetbrains.kotlin") {
                useVersion(Eoo.kotlinVersion)
            }

            if (requested.group == "io.springfox") {
                exclude("com.google.code.findbugs", "jsr305")
            }

            if (requested.group == "org.cassandraunit") {
                exclude("org.slf4j", "jcl-over-slf4j")
                exclude("org.slf4j", "slf4j-log4j12")
            }

            if (requested.group == "net.logstash.logback") {
                exclude("ch.qos.logback", "logback-core")
                exclude("ch.qos.logback", "logback-classic")
                exclude("ch.qos.logback", "logback-access")
            }
        }
    }
}
