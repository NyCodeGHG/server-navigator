plugins {
    kotlin("jvm")
    shadow
}

repositories {
    sonatype()
    spigot()
    paper()
    minecraftLibraries()
}

dependencies {
    compileOnly("org.spigotmc", "spigot-api", "1.16.5-R0.1-SNAPSHOT")
    compileOnly("com.mojang", "authlib", "2.1.28")
    implementation(project(":server-navigator-core"))
    implementation(project(":server-navigator-provider"))
    implementation("io.papermc", "paperlib", "1.0.6")
    implementation("net.kyori", "adventure-platform-bukkit", "4.0.0-SNAPSHOT")
    implementation("net.kyori", "adventure-api", "4.7.0")
    implementation("net.kyori", "adventure-extra-kotlin", "4.7.0")
}

tasks {
    jar {
        enabled = false
    }
    build {
        finalizedBy(shadowJar)
    }
}
