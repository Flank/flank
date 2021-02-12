package flank.scripts.ops.dependencies

import flank.scripts.ops.dependencies.common.Dependency
import flank.scripts.ops.dependencies.common.DependencyUpdate
import flank.scripts.ops.dependencies.common.outDatedDependencies
import java.io.File

internal fun File.updateDependencies(dependenciesFile: File, versionsFile: File) {
    versionsFile.updateVersions(
        dependencies = outDatedDependencies().getDependenciesToUpdate(dependenciesFile)
    )
}

private fun List<Dependency>.getDependenciesToUpdate(dependenciesFile: File): List<DependencyUpdate> {
    val outdatedDependenciesFileLines = dependenciesFile.getOutdatedDependenciesFileLines(
        outdatedDependenciesNames = map { it.groupWithName }
    )
    return map {
        DependencyUpdate(
            name = it.groupWithName,
            valName = outdatedDependenciesFileLines.matchingVersionVal(it.groupWithName),
            oldVersion = it.version,
            newVersion = it.versionToUpdate
        )
    }
}

internal val Dependency.groupWithName get() = "$group:$name:"

internal fun List<String>.matchingVersionVal(name: String) =
    find { it.contains(name) }?.findValName() ?: NOT_FOUND_VERSION

private fun String.findValName() = versionRegex.find(this)
    ?.value
    ?.split('.')
    ?.last()
    ?.replace("}\"", "")
    ?: NOT_FOUND_VERSION

private const val NOT_FOUND_VERSION = "!versionNotFound"
private val versionRegex = "(\\$\\{Versions\\.).*}\"".toRegex()

private fun File.getOutdatedDependenciesFileLines(
    outdatedDependenciesNames: List<String>
) = readLines().filter { line -> outdatedDependenciesNames.any { line.contains(it) } }
