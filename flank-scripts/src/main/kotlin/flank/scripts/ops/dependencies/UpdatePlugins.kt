package flank.scripts.ops.dependencies

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Collectors

internal fun File.updatePlugins(pluginsFile: File, versionsFile: File, buildGradleDirectory: String = "") {
    versionsFile.updateVersions(
        dependencies = getDependenciesUpdate(findPluginsValNames(pluginsFile), buildGradleDirectory)
    )
}

private fun getDependenciesUpdate(
    pluginsValNames: List<Pair<Dependency, String>>,
    buildGradleDirectory: String
) = findBuildGradleFiles(buildGradleDirectory)
    .getPluginsBlock()
    .fold(setOf<DependencyUpdate>()) { currentSet, pluginsBlock ->
        currentSet + pluginsBlock.findVersionsValNameFor(pluginsValNames).flatten().mapNotNull { it }
    }
    .toList()

private fun findBuildGradleFiles(buildGradleDirectory: String) =
    Files.walk(Paths.get(buildGradleDirectory))
        .filter { it.fileName.toString() == BUILD_GRADLE_FILE_NAME }
        .map { it.toFile() }
        .collect(Collectors.toList())

private fun List<File>.getPluginsBlock() =
    mapNotNull { pluginsBlockRegex.find(it.readText()) }
        .map { it.value }

private fun File.findPluginsValNames(pluginsFile: File) =
    outDatedDependencies()
        .filter { it.name?.contains("gradle.plugin") ?: false }
        .map { dependency -> dependency to dependency.group.findPluginValNames(pluginsFile) }
        .filter { (_, pluginVal) -> pluginVal.isNotEmpty() }

private fun String.findPluginValNames(pluginsFile: File) =
    pluginsFile.readLines()
        .find { line -> line.contains(this) }
        ?.pluginValName
        .orEmpty()

private fun String.findVersionsValNameFor(pluginValNames: List<Pair<Dependency, String>>) =
    split("\n")
        .map { line -> pluginValNames.getDependenciesUpdateFor(line) }

private fun List<Pair<Dependency, String>>.getDependenciesUpdateFor(line: String) =
    map { it.getDependencyUpdateIfPresent(line) }

private fun Pair<Dependency, String>.getDependencyUpdateIfPresent(line: String): DependencyUpdate? {
    val (dependency, pluginVal) = this
    return line.getDependencyValOrNull(pluginVal)?.let {
        DependencyUpdate(
            name = dependency.group,
            valName = it,
            oldVersion = dependency.version,
            newVersion = dependency.versionToUpdate
        )
    }
}

private fun String.getDependencyValOrNull(pluginVal: String) = takeIf { contains(pluginVal) }?.let { findValName() }

private fun String.findValName() = variableNameRegex.find(trim())?.value?.split(".")?.last()

private val String.pluginValName
    get() = trim().replace("const val ", "").split(" ").first()

private val pluginsBlockRegex = "plugins \\{(\\n)(\\s*.*\\n)*?}".toRegex()
private val variableNameRegex = ("Versions\\..+".toRegex())
private const val BUILD_GRADLE_FILE_NAME = "build.gradle.kts"
