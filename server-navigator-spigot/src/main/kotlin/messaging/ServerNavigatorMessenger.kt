@file:Suppress("UnstableApiUsage")

package de.nycode.servernavigator.platform.spigot.messaging

import com.google.common.io.ByteStreams
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener

object ServerNavigatorMessenger : PluginMessageListener {

    var selfNetworkName: String? = null
        private set

    override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray) {
        if (channel != "BungeeCord") {
            return
        }

        val input = ByteStreams.newDataInput(message)
        val subChannel = input.readUTF()

        if (subChannel == "GetServer") {
            selfNetworkName = input.readUTF()
        }
    }
}
