import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.FileInputStream
import java.util.*

buildscript {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://repo.spring.io/plugins-release") }
    }
}

plugins {
    kotlin("jvm") version "1.3.72"
    kotlin("plugin.spring") version "1.3.72"
//    id("org.jetbrains.kotlin.jvm") version "1.3.72"
//    id("org.jetbrains.kotlin.plugin.spring") version "1.3.72"

//    id("org.springframework.boot") version "2.2.8.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    id("org.jlleitschuh.gradle.ktlint") version "9.2.1"
    id("io.gitlab.arturbosch.detekt") version "1.10.0"
    id("org.sonarqube") version "2.8"
    id("io.spring.nohttp") version "0.0.5.RELEASE"
}

allprojects {
    group = "com.fahkap.eoo"
    version = "0.0.1-SNAPSHOT"
}

repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
    maven { url = uri("https://repo.spring.io/plugins-release") }
}

subprojects {

    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://repo.spring.io/plugins-release") }
    }

    apply {
        plugin("idea")
        plugin("java")
        plugin("maven")
        plugin("maven-publish")
        plugin("io.spring.dependency-management")
        plugin("io.spring.nohttp")
        plugin("org.jlleitschuh.gradle.ktlint")
        plugin("org.jlleitschuh.gradle.ktlint-idea")
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "11"
        }
        dependsOn(tasks.ktlintFormat)
    }

    tasks {
        named("install") {
            doLast {
                println(jar.get().archiveFileName.get())
            }
        }
    }
}

val sonarFile = FileInputStream("sonar-project.properties")
val sonarProperties = Properties()
sonarProperties.load(sonarFile)

sonarProperties.forEach { (key, value) ->
    sonarqube {
        properties {
            property(key as String, value)
        }
    }
}

nohttp {
    source.include("build.gradle", "README.md")
}

ktlint {
    ignoreFailures.value(true)
}
