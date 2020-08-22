package ftl.environment.android

import com.google.api.services.testing.model.AndroidVersion
import ftl.run.exception.FlankGeneralError

fun List<AndroidVersion>.getDescription(versionId: String) = findVersion(versionId)?.prepareDescription().orErrorMessage(versionId)

private fun List<AndroidVersion>.findVersion(versionId: String) = firstOrNull { it.id == versionId }

private fun AndroidVersion.prepareDescription() = """
    apiLevel: $apiLevel
    codeName: $codeName
    id: '$id'
    releaseDate:
      day: ${releaseDate.day}
      month: ${releaseDate.month}
      year: ${releaseDate.year}
""".trimIndent().addDataIfExists(tags).addVersion(versionString).trim()

private fun String.addVersion(versionString: String) = StringBuilder(this).appendLine("\nversionString: $versionString")
    .toString()

private fun String.addDataIfExists(data: List<String?>?) =
    if (!data.isNullOrEmpty()) StringBuilder(this).appendLine("\n$TAGS_HEADER:").appendDataToList(data)
    else this

private fun StringBuilder.appendDataToList(data: List<String?>) = apply {
    data.filterNotNull().forEach { item -> appendLine("- $item") }
}.toString().trim()

private fun String?.orErrorMessage(versionId: String) = this ?: throw FlankGeneralError("ERROR: '$versionId' is not a valid OS version")

private const val TAGS_HEADER = "tags"
