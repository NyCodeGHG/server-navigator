plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    api("org.jetbrains.kotlinx", "kotlinx-serialization-json", "1.1.0")
    api("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.4.3")
}
