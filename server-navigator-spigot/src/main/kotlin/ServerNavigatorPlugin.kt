package de.nycode.servernavigator.platform.spigot

import de.nycode.servernavigator.core.provider.ServerInformationProvider
import de.nycode.servernavigator.platform.spigot.commands.ServerNavigatorCommand
import de.nycode.servernavigator.platform.spigot.gui.ServerNavigatorGui
import de.nycode.servernavigator.platform.spigot.platform.SpigotPlatformConfiguration
import de.nycode.servernavigator.provider.Providers
import io.papermc.lib.PaperLib
import org.apache.logging.log4j.LogManager
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class ServerNavigatorPlugin : JavaPlugin() {

    lateinit var provider: ServerInformationProvider
        private set

    val gui = ServerNavigatorGui(this)

    override fun onEnable() {
        PaperLib.suggestPaper(this)
        initializeConfig()

        getCommand("servernavigator")!!.setExecutor(ServerNavigatorCommand(this))
        Bukkit.getPluginManager().registerEvents(gui, this)
        gui.startUpdater()
    }

    private fun initializeConfig() {
        saveDefaultConfig()
        reloadConfig()

        val providerKey = config.getString("provider") ?: orElse(
            "file",
            "Invalid provider in config.yml! Using file provider as fallback."
        )
        provider = Providers.ALL_PROVIDERS[providerKey] ?: orElse(
            "Invalid provider in config.yml! Using file provider as fallback.",
            Providers.ALL_PROVIDERS["file"]!!
        )

        val platform = SpigotPlatformConfiguration(config, dataFolder, LogManager.getLogger(logger.name))
        provider.loadConfig(platform)
    }

    private fun <T> orElse(message: String, value: T): T {
        logger.warning(message)
        return value
    }

}
