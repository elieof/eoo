

plugins {
    jacoco
    checkstyle

    kotlin("jvm")
    kotlin("plugin.spring")
//     id("org.jetbrains.kotlin.jvm")
//     id("org.jetbrains.kotlin.plugin.spring")

    id("org.sonarqube")
    id("io.gitlab.arturbosch.detekt")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
}

jacoco {
    toolVersion = "0.8.5"
}

checkstyle {
    toolVersion = "8.34"
    configFile = file("$rootDir/checkstyle.xml")
//    tasks.checkstyleTest.get().enabled = true
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

tasks.withType<Test> {
    useJUnitPlatform()

    testLogging {
        events("FAILED", "SKIPPED")
    }
    reports.html.isEnabled = false
}

tasks.withType<JacocoReport> {
    doFirst {
        println(sourceDirectories.from)
    }
    doLast {
        println(sourceDirectories.from)
    }

    executionData(tasks.test)
    classDirectories.from.addAll(sourceSets.main.get().output.classesDirs)
    sourceDirectories.from.addAll(sourceSets.main.get().allSource.srcDirs)

    reports {
        xml.isEnabled = true
    }
}

tasks.check {
    dependsOn(tasks.jacocoTestReport)
}

tasks.register<TestReport>("testReport") {
    destinationDir = file("$buildDir/reports/tests")
    reportOn(tasks.test)
}
