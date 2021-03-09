package de.nycode.servernavigator.provider.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PterodactylServerList(@SerialName("data") val servers: List<PterodactylServer>)
