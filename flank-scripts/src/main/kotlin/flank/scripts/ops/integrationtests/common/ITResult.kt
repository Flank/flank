package flank.scripts.ops.integrationtests.common

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = ITResultSerializer::class)
enum class ITResult {
    SUCCESS, FAILURE
}

object ITResultSerializer : KSerializer<ITResult> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ITResult", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder) = ITResult.valueOf(decoder.decodeString().uppercase())
    override fun serialize(encoder: Encoder, value: ITResult) = encoder.encodeString(value.name.lowercase())
}
