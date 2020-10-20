package flank.scripts.shell.utils

import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.Path

val currentPath = Paths.get("")
val rootDirectoryPath = goToRoot(currentPath)
val rootDirectoryFile = rootDirectoryPath.toFile()
val rootDirectoryPathString = rootDirectoryPath.toString()

fun goToRoot(startPath: Path): Path =
    if (startPath.isRoot()) startPath.toAbsolutePath() else goToRoot(startPath.toAbsolutePath().parent)

fun Path.isRoot() = Files.exists(Paths.get(toString(), "settings.gradle.kts"))

val testProjectsPath = Paths.get(rootDirectoryPathString, "test_projects").toString()
val androidTestProjectsPath = Paths.get(testProjectsPath, "android").toString()
val iOSTestProjectsPath = Paths.get(testProjectsPath, "ios").toString()
val flankFixturesTmpPath =
    Paths.get(rootDirectoryPathString, "test_runner", "src", "test", "kotlin", "ftl", "fixtures", "tmp").toString()
