package de.nycode.servernavigator.provider.model

import de.nycode.servernavigator.provider.model.PterodactylData.Attributes
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import java.util.*

internal val PterodactylModule = SerializersModule {
    polymorphic(PterodactylData::class) {
        subclass(PterodactylServer::class, PterodactylServer.serializer())
    }
}

@Serializable
sealed class PterodactylData<T : Attributes> {

    abstract val attributes: Attributes

    interface Attributes

}

@Serializable
@SerialName("server")
class PterodactylServer(override val attributes: Attributes) :
    PterodactylData<PterodactylServer.Attributes>() {
    @Serializable
    data class Attributes(
        @Contextual
        val uuid: UUID,
        val name: String,
        val description: String,
        val limits: PterodactylResourceLimits,
        val identifier: String,
        @SerialName("is_installing")
        val isInstalling: Boolean
    ) : PterodactylData.Attributes
}

@Serializable
class PterodactylResources(override val attributes: Attributes) :
    PterodactylData<PterodactylResources.Attributes>() {
    @Serializable
    data class Attributes(
        @SerialName("current_state")
        val currentState: String,
        val resources: PterodactylResourceValues
    ) : PterodactylData.Attributes
}

@Serializable
data class PterodactylResourceValues(
    @SerialName("memory_bytes")
    val memory: Long,
    @SerialName("cpu_absolute")
    val cpu: Double,
    @SerialName("disk_bytes")
    val disk: Long,
    @SerialName("network_tx_bytes")
    val networkTransmit: Long,
    @SerialName("network_rx_bytes")
    val networkReceive: Long
)
