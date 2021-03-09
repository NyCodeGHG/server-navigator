package de.nycode.servernavigator.core.model

import de.nycode.servernavigator.core.serialization.ResourceSerializer
import kotlinx.serialization.Serializable

typealias Resource = LongRange

/**
 * Represents physical or virtual usage and limits of server resources.
 */
@Serializable
data class ServerResources(
    @Serializable(with = ResourceSerializer::class)
    val memory: Resource,
    @Serializable(with = ResourceSerializer::class)
    val cpu: Resource,
    @Serializable(with = ResourceSerializer::class)
    val disk: Resource,
    val networkReceive: Long,
    val networkTransmit: Long
)
