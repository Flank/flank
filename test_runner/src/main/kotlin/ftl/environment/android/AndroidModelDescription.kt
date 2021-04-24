package ftl.environment.android

import ftl.api.DeviceModel
import ftl.run.exception.FlankGeneralError

fun List<DeviceModel.Android>.getDescription(modelId: String) = findModel(modelId)?.prepareDescription().orErrorMessage(modelId)

private fun List<DeviceModel.Android>.findModel(modelId: String) = firstOrNull { it.id == modelId }

private fun DeviceModel.Android.prepareDescription() = """
    brand: $brand
    codename: $codename
    form: $form
    formFactor: $formFactor
    id: $id
    manufacturer: $manufacturer
    name: $name
    screenDensity: $screenDensity
    screenX: $screenX
    screenY: $screenY
""".trimIndent()
    .appendList(SUPPORTED_ABIS_HEADER, supportedAbis)
    .appendList(SUPPORTED_VERSIONS_HEADER, supportedVersionIds)
    .appendList(TAGS_HEADER, tags)
    .appendThumbnail(thumbnailUrl).trim()

private fun String.appendList(header: String, items: List<String>?) =
    if (!items.isNullOrEmpty()) StringBuilder(this).appendLine().appendLine(header).appendItems(items).toString().trim()
    else this

private fun StringBuilder.appendItems(items: List<String>) = apply {
    items.forEach { appendLine("- $it") }
}

private fun String.appendThumbnail(thumbnailUrl: String?) =
    if (!thumbnailUrl.isNullOrBlank()) StringBuilder(this).appendLine("\n$THUBNAIL_URL_HEADER $thumbnailUrl").toString()
    else this

private fun String?.orErrorMessage(modelId: String) = this ?: throw FlankGeneralError("ERROR: '$modelId' is not a valid model")

private const val SUPPORTED_ABIS_HEADER = "supportedAbis:"
private const val SUPPORTED_VERSIONS_HEADER = "supportedVersionIds:"
private const val TAGS_HEADER = "tags:"
private const val THUBNAIL_URL_HEADER = "thumbnailUrl:"
