package de.nycode.servernavigator.provider

import de.nycode.servernavigator.core.model.ServerInformation
import de.nycode.servernavigator.core.model.ServerResources
import de.nycode.servernavigator.core.provider.PlatformConfiguration
import de.nycode.servernavigator.core.provider.ServerInformationProvider
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.util.*

class ServerInformationFileProvider : ServerInformationProvider() {

    override val configurationKey = "file"

    private lateinit var file: File

    override fun loadConfig(platform: PlatformConfiguration) {
        val filename = platform.getString("provider-file.file-name") ?: "servers.json"
        val serversFile = File(platform.getConfigRoot(), filename)

        if (!serversFile.exists()) {
            val json = Json.encodeToString(
                listOf(
                    ServerInformation(
                        UUID.randomUUID(),
                        "Awesome Server",
                        true,
                        "lobby",
                        "Awesome description",
                        false,
                        ServerResources(200L..2000L, 10L..100L, 150L..1000L, 1000, 1000)
                    )
                )
            )
            serversFile.createNewFile()
            serversFile.writeText(json)
        }
        file = serversFile
    }

    override suspend fun getServers(): List<ServerInformation> {
        val json = file.readText()
        return Json.decodeFromString(json)
    }
}
