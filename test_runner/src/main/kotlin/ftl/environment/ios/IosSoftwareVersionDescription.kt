package ftl.environment.ios

import com.google.api.services.testing.model.IosVersion

fun List<IosVersion>.getDescription(versionId: String) = findVersion(versionId)?.prepareDescription().createErrorMessage(versionId)

private fun List<IosVersion>.findVersion(versionId: String) = firstOrNull { it.id == versionId }

private fun IosVersion.prepareDescription() = """
    id: '$id'
    majorVersion: ${majorVersion.onUnknown()}
    minorVersion: ${minorVersion.onUnknown()}
""".trimIndent().addDataIfExists(SUPPORTED_VERSIONS_HEADER, supportedXcodeVersionIds).addDataIfExists(TAGS_HEADER, tags)

private fun Int?.onUnknown() = this?.toString() ?: UNKNOWN

private fun String.addDataIfExists(header: String, data: List<String?>?) =
    if (!data.isNullOrEmpty()) StringBuilder(this).appendln("\n$header:").appendDataToList(data)
    else this

private fun StringBuilder.appendDataToList(data: List<String?>) = apply {
    data.filterNotNull().forEach { item -> appendln("- $item") }
}.toString().trim()

private fun String?.createErrorMessage(versionId: String) = this ?: "ERROR: '$versionId' is not a valid OS version"

private const val TAGS_HEADER = "tags"
private const val SUPPORTED_VERSIONS_HEADER = "supportedXcodeVersionIds"
private const val UNKNOWN = "[Unknown]"
