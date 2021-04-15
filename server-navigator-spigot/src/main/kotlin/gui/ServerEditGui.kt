package de.nycode.servernavigator.platform.spigot.gui

import de.nycode.servernavigator.core.model.ServerInformation
import de.nycode.servernavigator.platform.spigot.ServerNavigatorPlugin
import de.nycode.servernavigator.platform.spigot.item.buildItem
import de.nycode.servernavigator.platform.spigot.utils.sendErrorTitle
import net.kyori.adventure.extra.kotlin.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.ChatColor.RED
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack

class ServerEditGui(private val plugin: ServerNavigatorPlugin) : Listener {

    private val serverEdits = mutableMapOf<ServerInformation, Player>()

    private val goBackItem = buildItem {
        material = Material.BARRIER
        displayName = "${RED}Zurück"
    }

    fun openEditGui(player: Player, serverInformation: ServerInformation) {
        if (!player.hasPermission("servernavigator.edit.${serverInformation.networkName}")) {
            player.closeInventory()
            player.sendErrorTitle {
                color(NamedTextColor.GRAY)
                content("Du darfst diesen Server nicht bearbeiten!")
            }
            return
        }

        if (serverInformation in serverEdits.keys) {
            player.closeInventory()
            player.sendErrorTitle {
                append(text {
                    color(NamedTextColor.GRAY)
                    content("Dieser Server wird gerade von ")
                })
                append(text {
                    color(NamedTextColor.RED)
                    content(serverEdits[serverInformation]?.name ?: "null")
                })
                append(text {
                    color(NamedTextColor.GRAY)
                    content(" bearbeitet")
                })
            }
            return
        }

        serverEdits[serverInformation] = player
        player.editInventory(serverInformation)
    }

    private fun Player.editInventory(serverInformation: ServerInformation) {
        with(openInventory.topInventory) {
            clear()
            val placeholder = ItemStack(Material.GRAY_STAINED_GLASS_PANE)
            for (slot in 0..8) {
                inventory.setItem(slot, placeholder)
            }
            for (slot in 46..53) {
                inventory.setItem(slot, placeholder)
            }
            setItem(45, goBackItem)
        }
    }

    fun isBeingEdited(serverInformation: ServerInformation) = serverEdits.containsKey(serverInformation)

    @EventHandler
    private fun onInventoryClose(event: InventoryCloseEvent) {
        if (event.player in serverEdits.values) {
            val server = serverEdits.filter { it.value == event.player }.keys.first()
            serverEdits.remove(server)
        }
    }

    @EventHandler
    private fun onInventoryClick(event: InventoryClickEvent) {
        if (event.whoClicked !in serverEdits.values) {
            return
        }

        if (event.currentItem?.isSimilar(goBackItem) == true) {
            plugin.navigatorGui.renderInventory(event.whoClicked as Player)
        }
    }

    fun isPlayerEditing(player: Player): Boolean {
        return serverEdits.filter { it.value == player }.keys.isNotEmpty()
    }
}

fun ServerInformation.isBeingEdited(): Boolean {
    return ServerNavigatorPlugin.instance.editGui.isBeingEdited(this)
}
