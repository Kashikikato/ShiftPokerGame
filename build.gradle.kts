import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.21" // Update to the latest stable version if available
    application
    jacoco
    id("io.gitlab.arturbosch.detekt") version "1.18.0-RC3"
    id("org.jetbrains.dokka") version "1.4.32"
    id("com.github.johnrengelman.shadow") version "7.1.1" // Add Shadow plugin for fat JAR
}

group = "edu.udo.cs.sopra"
version = "1.0"

repositories {
    mavenCentral()
}

application {
    mainClass.set("MainKt") // Ensure this matches your actual main class
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("tools.aqua:bgw-gui:0.9") // Ensure this dependency is correct and available

    // Uncomment and configure Ktor dependencies if needed
    // implementation("io.ktor:ktor-server-core:1.5.4")
    // implementation("io.ktor:ktor-server-netty:1.5.4")
    // implementation("io.ktor:ktor-websockets:1.5.4")
    // implementation("io.ktor:ktor-client-core:1.5.4")
    // implementation("io.ktor:ktor-client-cio:1.5.4")
    // implementation("io.ktor:ktor-client-websockets:1.5.4")
    // implementation("org.slf4j:slf4j-simple:1.7.30")

    testImplementation(kotlin("test-junit5"))
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "MainKt" // Ensure this matches your actual main class
    }
    archiveFileName.set("projekt1-1.0.jar") // Optional: Customize the JAR file name
}

tasks.shadowJar {
    archiveFileName.set("projekt1-fat.jar") // Customize the fat JAR file name
    manifest {
        attributes["Main-Class"] = "MainKt" // Ensure this matches your actual main class
    }
}

tasks.build {
    dependsOn(tasks.shadowJar) // Use shadowJar instead of jar for the build
}

tasks.distZip {
    archiveFileName.set("distribution.zip")
    destinationDirectory.set(layout.projectDirectory.dir("public"))
}

tasks.test {
    useJUnitPlatform()
    reports.html.outputLocation.set(layout.projectDirectory.dir("public/test"))
    finalizedBy(tasks.jacocoTestReport) // Report is always generated after tests run
    ignoreFailures = true
}

tasks.clean {
    delete.add("public")
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // Tests are required to run before generating the report
    reports {
        xml.required.set(false)
        csv.required.set(false)
        html.outputLocation.set(layout.projectDirectory.dir("public/coverage"))
    }
    classDirectories.setFrom(files(classDirectories.files.map {
        fileTree(it) {
            exclude(listOf("view/**", "entity/**", "Main*.*"))
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
