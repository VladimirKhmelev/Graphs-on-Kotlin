plugins {
    kotlin("jvm") version "1.9.23"
    id("org.jetbrains.compose") version "1.6.11"
    kotlin("plugin.serialization") version "1.9.23"
    jacoco
}

jacoco {
    toolVersion = "0.8.13"
}

sourceSets.main {
    resources.srcDirs("src/main/resources")
}

group = "org.example"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

configurations.all {
    exclude(group = "androidx.compose.compiler", module = "compiler")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation(compose.materialIconsExtended)

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.7.3")
    //implementation("org.jetbrains.compose.runtime:runtime:1.6.11")
    //implementation("org.jetbrains.compose.ui:ui:1.6.11")
    //implementation("org.jetbrains.compose.foundation:foundation:1.6.11")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.6.0")

    testImplementation(kotlin("test"))
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.2")
    testImplementation("org.hamcrest:hamcrest:2.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testImplementation("io.mockk:mockk:1.13.10")
}

tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required = true
        html.required = true
        csv.required = false
    }

    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it).apply {
                exclude(
                    "**/*Test*",
                    "**/model/**/*Dto*",
                    "**/generated/**"
                )
            }
        }
        )
    )
}


compose.desktop {
    application {
        mainClass = "graphApp.AppKt"
        nativeDistributions {
            targetFormats(org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi)
            packageName = "Graphs-Team7"
            packageVersion = "1.0.0"

            windows {
                shortcut = true
                menuGroup = "Graphs-Team7"
            }
        }
    }
}