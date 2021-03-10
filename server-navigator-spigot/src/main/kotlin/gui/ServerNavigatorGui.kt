package de.nycode.servernavigator.platform.spigot.gui

import de.nycode.servernavigator.core.model.ServerInformation
import de.nycode.servernavigator.platform.spigot.ServerNavigatorPlugin
import de.nycode.servernavigator.platform.spigot.item.buildItem
import de.nycode.servernavigator.platform.spigot.item.head
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.ChatColor.*
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

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
        player.openInventory(inventory)
        inventories[player] = inventory
    }

    private fun buildInventory(player: Player): Inventory {
        val inventory = Bukkit.createInventory(null, 54, "Servernavigator")

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

        return inventory
    }

    private fun updateInventory(player: Player) {
        val inventory = inventories[player] ?: return
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

    private fun ServerInformation.createItemStack(): ItemStack = buildItem {
        val color = if (online) GREEN else RED

        displayName = "$color$name"
        material = if (online) Material.GREEN_TERRACOTTA else Material.RED_TERRACOTTA

        lore.add(
            if (online) {
                "${BLUE}Linke-Maustaste: ${GRAY}Zum Server ${GREEN}verbinden"
            } else {
                "${BLUE}Linke-Maustaste: ${GRAY}Server ${GREEN}starten"
            }
        )

        lore.add("${BLUE}Rechte-Maustaste: ${GRAY}Server ${RED}bearbeiten")
        lore.add(BLUE.toString())

        lore.addAll(
            if (online) {
                listOf(
                    "${GREEN}Dieser Server läuft momentan!",
                    "${GREEN}Um dich mit ihm zu verbinden, klicke mit der linken Maustaste auf dieses Item."
                )
            } else {
                listOf(
                    "${RED}Dieser Server ist im Moment gestoppt!",
                    "${RED}Um ihn zu starten, klicke mit der linken Maustaste auf dieses Item."
                )
            }
        )

        if (online) {

            lore.add(BLUE.toString())
            lore.add("${BLUE}Server Ressourcen")
            lore.add("${GRAY}CPU-Auslastung: ${BLUE}${resources?.cpu?.first}%")

            lore.add(
                "${GRAY}Arbeitsspeicher: ${BLUE}${
                    String.format(
                        "%.2f",
                        resources?.memory?.first?.div(1000000.0)
                    )
                } MB"
            )
            lore.add(
                "${AQUA}von ${BLUE}${String.format("%.2f", resources?.memory?.last?.div(1000000.0))} MB"
            )

            val percentage =
                resources?.disk?.first?.toDouble()?.div(resources?.disk?.last?.toDouble() ?: return@buildItem)
            lore.add(
                "${GRAY}Festplatte: ${BLUE}${
                    String.format(
                        "%.2f",
                        percentage?.times(100.0)
                    )
                }% ${AQUA}von ${BLUE}${resources?.disk?.last?.div(1000000000.0)} GB"
            )
        }
    }

    @EventHandler
    private fun onInventoryClose(event: InventoryCloseEvent) {
        val player = event.player
        inventories.remove(player)
    }

    @EventHandler
    private fun onInventoryClick(event: InventoryClickEvent) {
        val inventory = event.clickedInventory
        if (inventory in inventories.values) {
            event.isCancelled = true
        }
    }

}
