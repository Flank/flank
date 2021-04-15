package flank.common

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

val currentPath = Paths.get("")
val rootDirectoryPath = goToRoot(currentPath)
val rootDirectoryPathString = rootDirectoryPath.toString()

tailrec fun goToRoot(startPath: Path): Path = when {
    startPath.isRoot() -> startPath.toAbsolutePath()
    startPath.toAbsolutePath().parent == null -> startPath.toAbsolutePath()
    else -> goToRoot(startPath.toAbsolutePath().parent)
}

fun Path.isRoot() = Files.exists(Paths.get(toString(), "settings.gradle.kts"))

val testProjectsPath = Paths.get(rootDirectoryPathString, "test_projects").toString()
val androidTestProjectsPath = Paths.get(testProjectsPath, "android").toString()
val iOSTestProjectsPath = Paths.get(testProjectsPath, "ios").toString()
val flankFixturesPath = Paths.get(rootDirectoryPathString, "test_runner", "src", "test", "kotlin", "ftl", "fixtures").toString()
val flankFixturesTmpPath = Paths.get(flankFixturesPath, "tmp").toString()
val flankFixturesIosTmpPath = Paths.get(flankFixturesTmpPath, "ios").toString()

val flankCommonRootPathString = Paths.get(rootDirectoryPathString, "common").toString()
