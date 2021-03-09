import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.kotlin.dsl.TaskContainerScope
import org.gradle.kotlin.dsl.maven
import org.gradle.plugin.use.PluginDependenciesSpec
import org.gradle.plugin.use.PluginDependencySpec

inline val PluginDependenciesSpec.shadow: PluginDependencySpec
    get() = id("com.github.johnrengelman.shadow")

fun RepositoryHandler.sonatype() = maven("https://oss.sonatype.org/content/repositories/snapshots/")

fun RepositoryHandler.spigot() = maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")

fun RepositoryHandler.paper() = maven("https://papermc.io/repo/repository/maven-public/")

fun RepositoryHandler.minecraftLibraries() = maven("https://libraries.minecraft.net/")
