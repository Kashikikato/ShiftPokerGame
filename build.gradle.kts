import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.21"
    application
    jacoco
    id("io.gitlab.arturbosch.detekt") version "1.18.0-RC3"
    id("org.jetbrains.dokka") version "1.4.32"
}

group = "edu.udo.cs.sopra"
version = "1.0"

repositories {
    mavenCentral()
}

application {
    mainClass.set("MainKt")
}

dependencies {
    testImplementation(kotlin("test-junit5"))

    // Ktor server dependencies
//    implementation("io.ktor:ktor-server-core:1.5.4")
//    implementation("io.ktor:ktor-server-netty:1.5.4")
//    implementation("io.ktor:ktor-websockets:1.5.4")
//    implementation("io.ktor:ktor-client-core:1.5.4")
//    implementation("io.ktor:ktor-client-cio:1.5.4")
//    implementation("io.ktor:ktor-client-websockets:1.5.4")
//    implementation("org.slf4j:slf4j-simple:1.7.30")
    testImplementation(kotlin("test-junit5"))
    implementation(group = "tools.aqua", name = "bgw-gui", version = "0.9")
}

tasks.distZip {
    archiveFileName.set("distribution.zip")
    destinationDirectory.set(layout.projectDirectory.dir("public"))
}

tasks.test {
    useJUnitPlatform()
    reports.html.outputLocation.set(layout.projectDirectory.dir("public/test"))
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
    ignoreFailures = true
}

tasks.clean {
    delete.add("public")
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
    reports {
        xml.required.set(false)
        csv.required.set(false)
        html.outputLocation.set(layout.projectDirectory.dir("public/coverage"))
    }

    classDirectories.setFrom(files(classDirectories.files.map {
        fileTree(it) {
            exclude(listOf("view/**", "entity/**",  "Main*.*"))
        }
    }))
}

detekt {
    toolVersion = "1.18.0-RC3"
    config = files("detektConfig.yml")

    reports {
        html {
            enabled = true
            reportsDir = file("public/detekt")
        }
        sarif {
            enabled = false
        }
    }
}

tasks.dokkaHtml.configure {
    outputDirectory.set(projectDir.resolve("public/dokka"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}
