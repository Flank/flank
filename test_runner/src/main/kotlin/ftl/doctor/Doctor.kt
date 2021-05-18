package ftl.doctor

import com.fasterxml.jackson.databind.JsonNode
import com.google.common.annotations.VisibleForTesting
import ftl.args.AndroidArgsCompanion
import ftl.args.ArgsHelper
import ftl.args.IArgs
import ftl.args.yml.MODEL_NODE
import ftl.args.yml.VERSION_NODE
import ftl.args.yml.devicesNode
import ftl.args.yml.notValidDevices
import ftl.config.loadAndroidConfig
import ftl.config.loadIosConfig
import ftl.domain.DoctorErrors
import ftl.domain.plus
import ftl.run.exception.FlankConfigurationError
import ftl.util.loadFile
import java.io.Reader
import java.nio.file.Path

fun validateYaml(args: IArgs.ICompanion, data: Path): DoctorErrors =
    if (!data.toFile().exists()) DoctorErrors(parsingErrors = listOf("Skipping yaml validation. No file at path $data"))
    else validateYaml(args, loadFile(data)) + preloadConfiguration(data, args is AndroidArgsCompanion)

@VisibleForTesting
internal fun validateYaml(args: IArgs.ICompanion, data: Reader): DoctorErrors =
    runCatching { ArgsHelper.yamlMapper.readTree(data) }
        .onFailure {
            return DoctorErrors(
                parsingErrors = listOf(
                    it.message?.replace(System.lineSeparator(), "\n") ?: "Unknown error when parsing tree"
                )
            )
        }
        .getOrNull()
        ?.run { validateYamlKeys(args) }
        ?: DoctorErrors.EMPTY

private fun JsonNode.validateYamlKeys(args: IArgs.ICompanion) = DoctorErrors(
    topLevelUnknownKeys = validateTopLevelKeys(args),
    nestedUnknownKeys = args.validArgs.map { (topLevelKey, validArgsKeys) ->
        (topLevelKey to validateNestedKeys(topLevelKey, validArgsKeys))
    }.filter { it.second.isNotEmpty() }.toMap(),
    invalidDevices = validateDevices().orEmpty()
)

private fun JsonNode.validateTopLevelKeys(args: IArgs.ICompanion): List<String> =
    (parseArgs().keys - args.validArgs.keys)
        .takeIf { it.isNotEmpty() }
        ?.toList()
        .orEmpty()

private fun JsonNode.parseArgs() = mutableMapOf<String, List<String>>().apply {
    for (child in fields()) {
        this[child.key] = child.value.fields().asSequence().map { it.key }.toList()
    }
}

private fun JsonNode.validateNestedKeys(topLevelKey: String, validArgsKeys: List<String>) =
    nestedKeysFor(topLevelKey) - validArgsKeys

private fun JsonNode.nestedKeysFor(topLevelKey: String) =
    this[topLevelKey]?.fields()?.asSequence()?.map { it.key }?.toList().orEmpty()

private fun preloadConfiguration(data: Path, isAndroid: Boolean) =
    try {
        if (isAndroid) loadAndroidConfig(data) else loadIosConfig(data)
        DoctorErrors.EMPTY
    } catch (e: FlankConfigurationError) {
        DoctorErrors(
            parsingErrors = listOf(e.message.orEmpty())
        )
    }

private fun JsonNode.validateDevices() =
    devicesNode?.notValidDevices?.withVersionNode?.map { device ->
        DoctorErrors.InvalidDevice(
            device[VERSION_NODE].asText("Unknown"),
            device[MODEL_NODE].asText("Unknown")
        )
    }

private val List<JsonNode>.withVersionNode
    get() = this.filter { it.has(VERSION_NODE) }
