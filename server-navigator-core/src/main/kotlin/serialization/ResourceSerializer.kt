package de.nycode.servernavigator.core.serialization

import de.nycode.servernavigator.core.model.Resource
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

object ResourceSerializer : KSerializer<Resource> {

    override val descriptor = PrimitiveSerialDescriptor("Resource", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Resource {
        val (first, last) = decoder.decodeString()
            .split("..")
            .map { it.toLong() }
        return Resource(first, last)
    }

    override fun serialize(encoder: Encoder, value: Resource) {
        encoder.encodeString(value.toString())
    }
}
