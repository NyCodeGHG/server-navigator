package de.nycode.servernavigator.platform.spigot.utils

import de.nycode.servernavigator.platform.spigot.ServerNavigatorPlugin
import net.kyori.adventure.extra.kotlin.text
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.title.Title
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.entity.Player

fun Player.sendErrorTitle(builder: TextComponent.Builder.() -> Unit) {
    playSound(location, Sound.ENTITY_WITHER_SHOOT, SoundCategory.MASTER, 1f, 1.75f)

    val component = text(builder)
    val title = Title.title(Component.empty(), component)
    ServerNavigatorPlugin.instance.adventure.player(this).showTitle(title)
}
