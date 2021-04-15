package de.nycode.servernavigator.platform.spigot.item

import de.nycode.servernavigator.core.model.ServerInformation
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

fun ServerInformation.createItemStack(): ItemStack = buildItem {
    val color = if (isRunning) ChatColor.GREEN else ChatColor.RED

    displayName = "$color$name"
    material = if (isRunning) Material.GREEN_TERRACOTTA else Material.RED_TERRACOTTA

    lore.add(
        if (isRunning) {
            "${ChatColor.BLUE}Linke-Maustaste: ${ChatColor.GRAY}Zum Server ${ChatColor.GREEN}verbinden"
        } else {
            "${ChatColor.BLUE}Linke-Maustaste: ${ChatColor.GRAY}Server ${ChatColor.GREEN}starten"
        }
    )

    lore.add("${ChatColor.BLUE}Rechte-Maustaste: ${ChatColor.GRAY}Server ${ChatColor.RED}bearbeiten")
    lore.add(ChatColor.BLUE.toString())

    lore.addAll(
        if (isRunning) {
            listOf(
                "${ChatColor.GREEN}Dieser Server läuft momentan!",
                "${ChatColor.GREEN}Um dich mit ihm zu verbinden, klicke mit der linken Maustaste auf dieses Item."
            )
        } else {
            listOf(
                "${ChatColor.RED}Dieser Server ist im Moment gestoppt!",
                "${ChatColor.RED}Um ihn zu starten, klicke mit der linken Maustaste auf dieses Item."
            )
        }
    )

    if (isRunning) {

        lore.add(ChatColor.BLUE.toString())
        lore.add("${ChatColor.BLUE}Server Ressourcen")
        lore.add("${ChatColor.GRAY}CPU-Auslastung: ${ChatColor.BLUE}${resources?.cpu?.first}%")

        lore.add(
            "${ChatColor.GRAY}Arbeitsspeicher: ${ChatColor.BLUE}${
                String.format(
                    "%.2f",
                    resources?.memory?.first?.div(1000000.0)
                )
            } MB"
        )
        lore.add(
            "${ChatColor.AQUA}von ${ChatColor.BLUE}${String.format("%.2f", resources?.memory?.last?.div(1000000.0))} MB"
        )

        val percentage =
            resources?.disk?.first?.toDouble()?.div(resources?.disk?.last?.toDouble() ?: return@buildItem)
        lore.add(
            "${ChatColor.GRAY}Festplatte: ${ChatColor.BLUE}${
                String.format(
                    "%.2f",
                    percentage?.times(100.0)
                )
            }% ${ChatColor.AQUA}von ${ChatColor.BLUE}${resources?.disk?.last?.div(1000000000.0)} GB"
        )
    }
}
