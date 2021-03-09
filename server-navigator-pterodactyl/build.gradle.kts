plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    val ktorVersion = "1.5.1"
    api("io.ktor", "ktor-client", ktorVersion)
    api("io.ktor", "ktor-client-okhttp", ktorVersion)
    api("io.ktor", "ktor-client-serialization", ktorVersion)

    implementation(project(":server-navigator-core"))
}
