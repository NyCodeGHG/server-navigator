package de.nycode.servernavigator.platform.spigot.platform

import de.nycode.servernavigator.core.provider.PlatformConfiguration
import org.apache.logging.log4j.Logger
import org.bukkit.configuration.Configuration
import java.io.File

class SpigotPlatformConfiguration(private val configuration: Configuration, private val pluginFolder: File, private val logger: Logger) :
    PlatformConfiguration {
    override fun getString(path: String): String? {
        return configuration.getString(path)
    }

    override fun set(path: String, value: Any) {
        configuration.set(path, value)
    }

    override fun getConfigRoot() = pluginFolder

    override fun info(text: String) = logger.info(text)

    override fun warn(text: String) = logger.warn(text)

    override fun error(text: String) = logger.error(text)
}
