package ftl.environment.ios

import com.google.api.services.testing.model.IosVersion
import ftl.run.exception.FlankGeneralError

fun List<IosVersion>.getDescription(versionId: String) = findVersion(versionId)?.prepareDescription().orErrorMessage(versionId)

private fun List<IosVersion>.findVersion(versionId: String) = firstOrNull { it.id == versionId }

private fun IosVersion.prepareDescription() = """
    id: '$id'
    majorVersion: $majorVersion
    minorVersion: $minorVersion
""".trimIndent().addDataIfExists(SUPPORTED_VERSIONS_HEADER, supportedXcodeVersionIds).addDataIfExists(TAGS_HEADER, tags)

private fun String.addDataIfExists(header: String, data: List<String?>?) =
    if (!data.isNullOrEmpty()) StringBuilder(this).appendLine("\n$header:").appendDataToList(data)
    else this

private fun StringBuilder.appendDataToList(data: List<String?>) = apply {
    data.filterNotNull().forEach { item -> appendLine("- $item") }
}.toString().trim()

private fun String?.orErrorMessage(versionId: String) = this ?: throw FlankGeneralError("ERROR: '$versionId' is not a valid OS version")

private const val TAGS_HEADER = "tags"
private const val SUPPORTED_VERSIONS_HEADER = "supportedXcodeVersionIds"
