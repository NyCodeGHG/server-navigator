package de.nycode.servernavigator.core.provider

import de.nycode.servernavigator.core.model.ServerInformation

abstract class ServerInformationProvider {

    abstract val configurationKey: String

    abstract fun loadConfig(platform: PlatformConfiguration)

    abstract suspend fun getServers(): List<ServerInformation>
}
