package ftl.doctor

import ftl.args.ArgsHelper
import ftl.args.IArgsCompanion
import java.nio.file.Files
import java.nio.file.Path

object Doctor {
    fun validateYaml(args: IArgsCompanion, data: Path): String {
        if (!data.toFile().exists()) return "Skipping yaml validation. No file at path $data"
        return validateYaml(args, String(Files.readAllBytes(data)))
    }

    fun validateYaml(args: IArgsCompanion, data: String): String {
        var result = ""
        val parsed = ArgsHelper.yamlMapper.readTree(data)

        val validArgs = args.validArgs
        val parsedArgs = mutableMapOf<String, List<String>>()

        for (child in parsed.fields()) {
            val key = child.key
            val values = mutableListOf<String>()
            child.value.fields().forEach { values.add(it.key) }

            parsedArgs[key] = values
        }

        val unknownTopLevelKeys = parsedArgs.keys - validArgs.keys
        if (unknownTopLevelKeys.isNotEmpty()) result += "Unknown top level keys: $unknownTopLevelKeys\n"

        validArgs.forEach { (topLevelKey, keyList) ->
            val parsedKeys = mutableListOf<String>()
            parsed[topLevelKey]?.fields()?.forEach { parsedKeys.add(it.key) }
            val unknownKeys = parsedKeys - keyList
            if (unknownKeys.isNotEmpty()) result += "Unknown keys in $topLevelKey -> $unknownKeys\n"
        }

        return result
    }
}
