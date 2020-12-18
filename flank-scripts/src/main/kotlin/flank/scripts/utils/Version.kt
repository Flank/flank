package flank.scripts.utils

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object VersionSerializer : KSerializer<Version> {
    override val descriptor = PrimitiveSerialDescriptor("Version", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Version = parseToVersion(decoder.decodeString())

    override fun serialize(encoder: Encoder, value: Version) = encoder.encodeString(value.toString())
}

@Serializable(with = VersionSerializer::class)
data class Version(
    private val major: Int? = null,
    private val minor: Int? = null,
    private val micro: Int? = null,
    private val patch: Int? = null,
    private val qualifier: String? = null
) : Comparable<Version> {
    private val hasSuffix = major != null && qualifier != null

    override operator fun compareTo(other: Version): Int = when {
        major differs other.major -> compareValuesBy(major, other.major, { it ?: 0 })
        minor differs other.minor -> compareValuesBy(minor, other.minor, { it ?: 0 })
        micro differs other.micro -> compareValuesBy(micro, other.micro, { it ?: 0 })
        patch differs other.patch -> compareValuesBy(patch, other.patch, { it ?: 0 })
        else -> nullsLast<String>().compare(qualifier, other.qualifier)
    }

    private infix fun Int?.differs(other: Int?) = (this ?: 0) != (other ?: 0)

    override fun toString(): String = listOfNotNull(major, minor, micro, patch)
        .joinToString(".") + "${if (hasSuffix) "-" else ""}${qualifier ?: ""}"
}

fun parseToVersion(versionString: String): Version {
    val groups = "(\\d*)\\.?(\\d*)\\.?(\\d*)\\.?(\\d*)[-.]?([a-zA-Z0-9_.-]*)".toRegex().find(versionString)?.groupValues
    return if (groups == null) Version(qualifier = versionString)
    else Version(
        major = groups[1].toIntOrNull(),
        minor = groups[2].toIntOrNull(),
        micro = groups[3].toIntOrNull(),
        patch = groups[4].toIntOrNull(),
        qualifier = if (groups[5].isNotBlank()) groups[5] else null
    )
}
