package ftl.presentation.cli.firebase.test

import flank.common.logLn
import ftl.args.yml.DEVICES_NODE
import ftl.args.yml.GCLOUD_NODE
import ftl.args.yml.VERSION_NODE
import ftl.args.yml.YamlDeprecated
import ftl.args.yml.fixDevices
import ftl.domain.RunDoctor
import ftl.run.exception.YmlValidationError
import java.nio.file.Path

fun processValidation(ymlPath: Path) {
    logLn("Trying to fix yml file")
    if (YamlDeprecated.modify(ymlPath)) {
        throw YmlValidationError("Invalid yml file, unable to fix yml file")
    }
    fixDevices(ymlPath)
}

fun RunDoctor.Error.summary(): String =
    if (this == RunDoctor.Error.EMPTY) "Valid yml file"
    else buildString {
        parsingErrors.forEach { appendLine(it) }
        invalidDevices
            .map { "Warning: Version should be string $GCLOUD_NODE -> $DEVICES_NODE[${it.model}] -> $VERSION_NODE[${it.version}]" }
            .forEach { appendLine(it) }
        if (topLevelUnknownKeys.isNotEmpty()) appendLine("Unknown top level keys: $topLevelUnknownKeys")
        nestedUnknownKeys.forEach { (topLevelKey, keys) ->
            appendLine("Unknown keys in $topLevelKey -> $keys")
        }
    }.trim()
