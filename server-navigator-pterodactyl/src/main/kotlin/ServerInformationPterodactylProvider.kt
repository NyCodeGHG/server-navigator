package de.nycode.servernavigator.provider

import de.nycode.servernavigator.core.model.Resource
import de.nycode.servernavigator.core.model.ServerInformation
import de.nycode.servernavigator.core.model.ServerResources
import de.nycode.servernavigator.core.provider.PlatformConfiguration
import de.nycode.servernavigator.core.provider.ServerInformationProvider
import de.nycode.servernavigator.core.serialization.UUIDtoStringSerializer
import de.nycode.servernavigator.provider.model.PterodactylModule
import de.nycode.servernavigator.provider.model.PterodactylResources
import de.nycode.servernavigator.provider.model.PterodactylServer
import de.nycode.servernavigator.provider.model.PterodactylServerList
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus
import java.util.*
import kotlinx.serialization.json.Json as TheOtherJsonThing

class ServerInformationPterodactylProvider : ServerInformationProvider() {

    private var host: String? = null
    private var apiKey: String? = null

    override val configurationKey = "pterodactyl"

    private val client = HttpClient(OkHttp) {
        install(JsonFeature) {
            val json = TheOtherJsonThing {
                serializersModule = PterodactylModule + SerializersModule {
                    contextual(UUID::class, UUIDtoStringSerializer)
                }
                classDiscriminator = "object"
                ignoreUnknownKeys = true
            }
            serializer = KotlinxSerializer(json)
        }
        defaultRequest {
            pterodactylAuthenticationHeaders()
        }
    }

    override fun loadConfig(platform: PlatformConfiguration) {
        host = platform.getString("provider-pterodactyl.host") ?: errorMessage(
            platform,
            "Host is not given! Provider configuration will not work!"
        )
        apiKey = platform.getString("provider-pterodactyl.api-key") ?: errorMessage(
            platform,
            "Api-Key is not given! Provider configuration will not work!"
        )
    }

    override suspend fun getServers(): List<ServerInformation> {
        val servers = client.get<PterodactylServerList>(this@ServerInformationPterodactylProvider.host!!) {
            url {
                path("api", "client")
            }
        }.servers
        val serverInformationList = mutableListOf<ServerInformation>()

        coroutineScope {
            servers
                .filterNot { it.attributes.isInstalling }
                .forEach { pterodactylServer ->
                    launch {
                        val resources =
                            client.get<PterodactylResources>(this@ServerInformationPterodactylProvider.host!!) {
                                url {
                                    path(
                                        "api",
                                        "client",
                                        "servers",
                                        pterodactylServer.attributes.identifier,
                                        "resources"
                                    )
                                }
                            }
                        val (memory, cpu, disk, networkTransmit, networkReceive) = resources.attributes.resources
                        val (uuid, name, description, limits, _, isInstalling) = pterodactylServer.attributes

                        val serverResources = ServerResources(
                            Resource(memory, limits.memory * 1000 * 1000),
                            Resource(cpu.toLong(), limits.cpu),
                            Resource(disk, limits.disk * 1000 * 1000),
                            networkReceive,
                            networkTransmit
                        )
                        val server = ServerInformation(
                            uuid,
                            name,
                            resources.attributes.currentState == "running",
                            "todo xd",
                            description,
                            isInstalling,
                            serverResources
                        )

                        serverInformationList.add(server)
                    }
                }
        }
        return serverInformationList
    }

    private fun <T> errorMessage(platform: PlatformConfiguration, message: String): T? {
        platform.error(message)
        return null
    }

    private fun HttpRequestBuilder.pterodactylAuthenticationHeaders() {
        header(HttpHeaders.Authorization, "Bearer $apiKey")
    }
}
