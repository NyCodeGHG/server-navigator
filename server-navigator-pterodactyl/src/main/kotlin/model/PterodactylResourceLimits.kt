package de.nycode.servernavigator.provider.model

import kotlinx.serialization.Serializable

@Serializable
data class PterodactylResourceLimits(
    val memory: Long,
    val disk: Long,
    val cpu: Long
)
