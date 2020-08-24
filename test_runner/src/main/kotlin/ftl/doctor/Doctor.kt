package ftl.doctor

import com.fasterxml.jackson.databind.JsonNode
import com.google.common.annotations.VisibleForTesting
import ftl.args.AndroidArgsCompanion
import ftl.args.ArgsHelper
import ftl.args.IArgs
import ftl.config.loadAndroidConfig
import ftl.config.loadIosConfig
import ftl.run.exception.FlankConfigurationError
import ftl.util.loadFile
import java.io.Reader
import java.lang.StringBuilder
import java.nio.file.Path

fun validateYaml(args: IArgs.ICompanion, data: Path) =
    if (!data.toFile().exists()) "Skipping yaml validation. No file at path $data"
    else validateYaml(args, loadFile(data)) + preloadConfiguration(data, args is AndroidArgsCompanion)

@VisibleForTesting
internal fun validateYaml(args: IArgs.ICompanion, data: Reader) =
    runCatching { ArgsHelper.yamlMapper.readTree(data) }
        .onFailure { return it.message ?: "Unknown error when parsing tree" }
        .getOrNull()
        ?.run { validateYamlKeys(args) }
        .orEmpty()

private fun JsonNode.validateYamlKeys(args: IArgs.ICompanion) = StringBuilder().apply {
    append(validateTopLevelKeys(args))
    args.validArgs.forEach { (topLevelKey, validArgsKeys) ->
        append(validateNestedKeys(topLevelKey, validArgsKeys))
    }
}.toString()

private fun JsonNode.validateTopLevelKeys(args: IArgs.ICompanion) =
    (parseArgs().keys - args.validArgs.keys)
        .takeIf { it.isNotEmpty() }
        ?.let { unknownKeys -> "Unknown top level keys: $unknownKeys\n" }
        .orEmpty()

private fun JsonNode.parseArgs() = mutableMapOf<String, List<String>>().apply {
    for (child in fields()) {
        this[child.key] = child.value.fields().asSequence().map { it.key }.toList()
    }
}

private fun JsonNode.validateNestedKeys(topLevelKey: String, validArgsKeys: List<String>) =
    nestedKeysFor(topLevelKey)
        .minus(validArgsKeys)
        .takeIf { it.isNotEmpty() }
        ?.let { "Unknown keys in $topLevelKey -> $it\n" }
        .orEmpty()

private fun JsonNode.nestedKeysFor(topLevelKey: String) =
    this[topLevelKey]?.fields()?.asSequence()?.map { it.key }?.toList().orEmpty()

private fun preloadConfiguration(data: Path, isAndroid: Boolean) =
    try {
        if (isAndroid) loadAndroidConfig(data) else loadIosConfig(data)
        ""
    } catch (e: FlankConfigurationError) {
        e.message
    }
