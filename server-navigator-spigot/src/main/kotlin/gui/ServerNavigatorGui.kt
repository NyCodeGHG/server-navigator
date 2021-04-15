package de.nycode.servernavigator.platform.spigot.gui

import com.google.common.io.ByteStreams
import de.nycode.servernavigator.core.model.ServerInformation
import de.nycode.servernavigator.platform.spigot.ServerNavigatorPlugin
import de.nycode.servernavigator.platform.spigot.item.buildItem
import de.nycode.servernavigator.platform.spigot.item.createItemStack
import de.nycode.servernavigator.platform.spigot.item.head
import de.nycode.servernavigator.platform.spigot.messaging.ServerNavigatorMessenger
import de.nycode.servernavigator.platform.spigot.utils.sendErrorTitle
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.kyori.adventure.extra.kotlin.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.ChatColor.*
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

@Suppress("UnstableApiUsage")
class ServerNavigatorGui(private val plugin: ServerNavigatorPlugin) : Listener {

    private val inventories = mutableMapOf<Player, Inventory>()
    private var servers: List<ServerInformation> = emptyList()

    fun startUpdater() {
        Bukkit.getScheduler().runTaskTimer(plugin, Runnable {
            GlobalScope.launch {
                servers = plugin.provider.getServers().sortedBy { it.name }
                inventories.keys.forEach { updateInventory(it) }
            }
        }, 0, 20)
    }

    fun openGui(player: Player) {
        val inventory = buildInventory(player)
        renderInventory(player)
        player.openInventory(inventory)
        inventories[player] = inventory
    }

    private fun buildInventory(player: Player): Inventory {
        return Bukkit.createInventory(null, 54, "Servernavigator")
    }

    fun renderInventory(player: Player) {
        val inventory = inventories[player] ?: return
        inventory.clear()

        val placeholder = ItemStack(Material.GRAY_STAINED_GLASS_PANE)
        for (slot in 0..8) {
            inventory.setItem(slot, placeholder)
        }
        for (slot in 46..52) {
            inventory.setItem(slot, placeholder)
        }

        val loadingPlaceholder = buildItem {
            material = Material.BARRIER
            displayName = "${DARK_RED}Wird geladen..."
            lore = mutableListOf(
                "${GRAY}Dieses Item wird noch geladen..."
            )
        }
        inventory.setItem(45, loadingPlaceholder)
        inventory.setItem(53, loadingPlaceholder)

        GlobalScope.launch {
            val pageLeftHead = buildItem {
                displayName = "${YELLOW}Nach Links"
                head {
                    name = "MHF_ArrowLeft"
                }
            }

            val pageRightHead = buildItem {
                displayName = "${YELLOW}Nach Rechts"
                head {
                    name = "MHF_ArrowRight"
                }
            }
            inventory.setItem(45, pageLeftHead)
            inventory.setItem(53, pageRightHead)
        }

        updateInventory(player)
    }

    private fun updateInventory(player: Player) {
        val inventory = inventories[player] ?: return
        if (plugin.editGui.isPlayerEditing(player)) {
            return
        }

        servers
            .map { it.createItemStack() }
            .take(35)
            .forEachIndexed { index, item ->
                inventory.setItem(9 + index, item)
            }
        for (i in servers.size + 9..44) {
            inventory.setItem(i, ItemStack(Material.AIR))
        }
    }

    @EventHandler
    private fun onInventoryClose(event: InventoryCloseEvent) {
        val player = event.player
        inventories.remove(player)
    }

    @EventHandler
    private fun onInventoryClick(event: InventoryClickEvent) {
        val inventory = event.clickedInventory ?: return
        val player = event.whoClicked as Player

        if (inventory in inventories.values) {
            event.isCancelled = true
        } else {
            return
        }

        if (event.currentItem?.type in arrayOf(Material.GREEN_TERRACOTTA, Material.RED_TERRACOTTA)) {
            val server = servers.find { it.name == stripColor(event.currentItem?.itemMeta?.displayName) } ?: return

            val action = when {
                event.isRightClick -> ServerAction.EDIT
                server.isRunning && !event.isRightClick -> ServerAction.JOIN
                else -> ServerAction.START
            }

            when (action) {
                ServerAction.START -> {
                }
                ServerAction.JOIN -> {
                    connectPlayer(player, server)
                }
                ServerAction.EDIT -> {
                    plugin.editGui.openEditGui(player, server)
                }
            }
        }
    }

    private fun connectPlayer(
        player: Player,
        server: ServerInformation
    ) {
        if (server.networkName == ServerNavigatorMessenger.selfNetworkName) {
            player.closeInventory()
            player.sendErrorTitle {
                color(NamedTextColor.RED)
                content("Du befindest dich bereits auf diesem Server!")
            }
            return
        }

        if (!player.hasPermission("servernavigator.join.${server.networkName}")) {
            player.closeInventory()
            player.sendErrorTitle {
                append(text {
                    color(NamedTextColor.GRAY)
                    content("Du darfst den Server ")
                })
                append(text {
                    color(NamedTextColor.RED)
                    content(server.name)
                })
                append(text {
                    color(NamedTextColor.GRAY)
                    content(" nicht betreten!")
                })
            }
            return
        }

        val out = ByteStreams.newDataOutput()
        out.writeUTF("GetServer")

        val component = text {
            append(text {
                color(NamedTextColor.GRAY)
                content("Du wirst zum Server ")
            })
            append(text {
                color(NamedTextColor.GREEN)
                content(server.name)
            })
            append(text {
                color(NamedTextColor.RED)
                content(" verbunden!")
            })
        }
        plugin.adventure.player(player).sendMessage(component)
        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray())
    }

    @EventHandler
    private fun onPlayerJoin(event: PlayerJoinEvent) {
        if (ServerNavigatorMessenger.selfNetworkName == null) {
            Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                val out = ByteStreams.newDataOutput()
                out.writeUTF("GetServer")
                event.player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray())
            }, 10)
        }
    }
}
