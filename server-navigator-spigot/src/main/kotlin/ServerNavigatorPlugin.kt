package de.nycode.servernavigator.platform.spigot

import de.nycode.servernavigator.core.provider.ServerInformationProvider
import de.nycode.servernavigator.platform.spigot.commands.ServerNavigatorCommand
import de.nycode.servernavigator.platform.spigot.gui.ServerEditGui
import de.nycode.servernavigator.platform.spigot.gui.ServerNavigatorGui
import de.nycode.servernavigator.platform.spigot.messaging.ServerNavigatorMessenger
import de.nycode.servernavigator.platform.spigot.platform.SpigotPlatformConfiguration
import de.nycode.servernavigator.provider.Providers
import io.papermc.lib.PaperLib
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import org.apache.logging.log4j.LogManager
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class ServerNavigatorPlugin : JavaPlugin() {

    companion object {
        lateinit var instance: ServerNavigatorPlugin
            private set
    }

    lateinit var provider: ServerInformationProvider
        private set

    lateinit var adventure: BukkitAudiences
        private set


    val navigatorGui = ServerNavigatorGui(this)
    val editGui = ServerEditGui(this)

    override fun onEnable() {
        instance = this
        PaperLib.suggestPaper(this)
        initializeConfig()

        adventure = BukkitAudiences.create(this)

        getCommand("servernavigator")!!.setExecutor(ServerNavigatorCommand(this))
        Bukkit.getPluginManager().registerEvents(navigatorGui, this)
        navigatorGui.startUpdater()

        server.messenger.registerOutgoingPluginChannel(this, "BungeeCord")
        server.messenger.registerIncomingPluginChannel(this, "BungeeCord", ServerNavigatorMessenger)
    }

    override fun onDisable() {
        adventure.close()
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
