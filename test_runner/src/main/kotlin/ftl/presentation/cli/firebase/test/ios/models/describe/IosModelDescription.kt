package ftl.presentation.cli.firebase.test.ios.models.describe

import ftl.api.DeviceModel
import ftl.run.exception.FlankGeneralError

fun DeviceModel.Ios.prepareDescription() = "".appendList(DEVICE_CAPABILITIES_HEADER, deviceCapabilities)
    .appendModelBasicData(this)
    .appendList(SUPPORTED_VERSIONS_HEADER, supportedVersionIds)
    .appendList(TAGS_HEADER, tags)
    .trim()

private fun String.appendList(header: String, items: List<String>?) =
    if (!items.isNullOrEmpty()) StringBuilder(this).appendLine(header).appendItems(items).toString()
    else this

private fun StringBuilder.appendItems(items: List<String>) = apply {
    items.forEach { appendLine("- $it") }
}

private fun String.appendModelBasicData(model: DeviceModel.Ios) = StringBuilder(this).appendLine(
    """
formFactor: ${model.formFactor}
id: ${model.id}
name: ${model.name}
screenDensity: ${model.screenDensity}
screenX: ${model.screenX}
screenY: ${model.screenY}
    """.trimIndent()
).toString()

private fun String?.orErrorMessage(modelId: String) =
    this ?: throw FlankGeneralError("ERROR: '$modelId' is not a valid model")

private const val DEVICE_CAPABILITIES_HEADER = "deviceCapabilities:"
private const val SUPPORTED_VERSIONS_HEADER = "supportedVersionIds:"
private const val TAGS_HEADER = "tags:"
