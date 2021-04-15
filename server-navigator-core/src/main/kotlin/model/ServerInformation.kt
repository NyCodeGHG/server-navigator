package de.nycode.servernavigator.core.model

import de.nycode.servernavigator.core.serialization.UUIDtoStringSerializer
import kotlinx.serialization.Serializable
import java.util.*

/**
 * Represents a snapshot of a server including [resources]
 * @property uuid uuid of the server
 * @property name name of the server
 * @property networkName name of the server in the corresponding bungeecord or velocity network used for sending players back and forth
 * @property description description of the server
 * @property resources Limits and Usage of server resources
 **/
@Serializable
data class ServerInformation(
    @Serializable(with = UUIDtoStringSerializer::class)
    val uuid: UUID,
    val name: String,
    val isRunning: Boolean,
    val networkName: String,
    val description: String,
    val isInstalling: Boolean = false,
    val resources: ServerResources?
)
