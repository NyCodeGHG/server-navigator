package de.nycode.servernavigator.platform.spigot.commands

import de.nycode.servernavigator.platform.spigot.ServerNavigatorPlugin
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ServerNavigatorCommand(private val serverNavigatorPlugin: ServerNavigatorPlugin) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            return true
        }
        serverNavigatorPlugin.gui.openGui(sender)
        return true
    }
}
