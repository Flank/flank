package flank.scripts.dependencies.update

import java.io.File

fun File.updateDependencies(dependenciesFile: File, versionsFile: File) {
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

private fun File.getOutdatedDependenciesFileLines(
    outdatedDependenciesNames: List<String>
) = readLines().filter { line -> outdatedDependenciesNames.any { line.contains(it) } }
