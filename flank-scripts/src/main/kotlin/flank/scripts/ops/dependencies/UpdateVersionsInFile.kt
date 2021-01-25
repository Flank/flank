package flank.scripts.ops.dependencies

import flank.common.withNewLineAtTheEnd
import java.io.File

fun File.updateVersions(dependencies: List<DependencyUpdate>) =
    writeText(
        readLines()
            .fold(listOf<String>()) { actual, line -> actual + line.getInsertLine(dependencies) }
            .joinToString(System.lineSeparator())
            .withNewLineAtTheEnd()
    )

private fun String.getInsertLine(
    dependencies: List<DependencyUpdate>
) = dependencies
    .find { containsValDeclaration(it) }
    ?.let {
        println("Updated dependency ${it.name} from ${it.oldVersion} to ${it.newVersion}")
        replaceFirst(it.oldVersion.toString(), it.newVersion.toString())
    }
    ?: this

private fun String.containsValDeclaration(
    dependencyUpdate: DependencyUpdate
) = contains("${dependencyUpdate.valName} = \"${dependencyUpdate.oldVersion}\"")
