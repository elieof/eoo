import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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

//    id("org.springframework.boot") version "2.2.8.RELEASE" apply false
    id("io.spring.dependency-management") version "1.0.9.RELEASE" apply false
    id("org.jlleitschuh.gradle.ktlint") version "9.2.1"
    id("io.gitlab.arturbosch.detekt") version "1.10.0"
    id("org.sonarqube") version "2.8" apply false
    id("io.spring.nohttp") version "0.0.5.RELEASE"
    id("org.jetbrains.dokka") version "0.10.1" apply false
    signing
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
        plugin("org.jetbrains.dokka")
        plugin("signing")
    }

    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    val dokkaTasks = tasks.withType<DokkaTask> {
        outputFormat = "html"
        outputDirectory = "$buildDir/javadoc"

    }

    val dokkaJar by tasks.creating(Jar::class) {
        group = JavaBasePlugin.DOCUMENTATION_GROUP
        description = "Assembles Kotlin docs with Dokka"
        archiveClassifier.set("javadoc")
        from(dokkaTasks)
    }

    val sourcesJar by tasks.creating(Jar::class) {
        archiveClassifier.set("sources")
        from(sourceSets.main.get().allSource)
    }

    val projectName = name
    val projectDescription = description
    val repoUrl = "https://github.com/elieof/eoo"
    configure<PublishingExtension> {
        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/elieof/eoo")
                credentials {
                    username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                    password = project.findProperty("gpr.key") as String? ?: System.getenv("PASSWORD")
                }
            }
//            maven {
//                name = "OSSRH"
//                val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
//                val snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots")
//                url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
//                credentials {
//                    username = project.findProperty("ossrh.user") as String? ?: System.getenv("MAVEN_USERNAME")
//                    password = project.findProperty("ossrh.password") as String? ?: System.getenv("MAVEN_PASSWORD")
//                }
//            }
        }
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
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
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
                val signingKeyId: String? by project
                val signingKey: String? by project
                val signingPassword: String? by project
                useInMemoryPgpKeys(
                    signingKeyId ?: System.getenv("SIGNING_KEY_ID"),
                    signingKey ?: System.getenv("SIGNING_KEY"),
                    signingPassword ?: System.getenv("SIGNING_PASSWORD")
                )
                sign(mavenPublication.get())
            }
        }
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

    ktlint {
        ignoreFailures.value(true)
    }
}

nohttp {
    source.include("build.gradle", "README.md")
}

ktlint {
    ignoreFailures.value(true)
}
