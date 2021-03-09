import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version BuildConstants.KOTLIN_VERSION apply false
    kotlin("plugin.serialization") version BuildConstants.KOTLIN_VERSION apply false
    shadow version "6.1.0" apply false
}

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    tasks {
        withType<KotlinCompile> {
            kotlinOptions.jvmTarget = "1.8"
        }
    }
}
