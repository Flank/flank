@file:Repository("https://dl.bintray.com/jakubriegel/kotlin-shell")
@file:DependsOn("eu.jrie.jetbrains:kotlin-shell-core:0.2.1")
@file:DependsOn("org.slf4j:slf4j-simple:1.7.28")
@file:Import("GradleCommand.kt")
@file:Import("PathHelper.kt")
@file:CompilerOptions("-Xopt-in=kotlin.RequiresOptIn")
@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

import eu.jrie.jetbrains.kotlinshell.shell.*
import java.nio.file.Path
import java.nio.file.Paths

val gradleCommand = createGradleCommand(
    workingDir = rootDirectoryPath,
    options = listOf(":test_runner:clean", ":test_runner:assemble", ":test_runner:shadowJar")
)

val flankDirectory: Path = Paths.get(rootDirectoryPath, "test_runner")

shell {
    gradleCommand()
}

Paths.get(flankDirectory.toString(), "build", "libs", "flank.jar").toFile()
    .copyTo(Paths.get(flankDirectory.toString(), "bash", "flank.jar").toFile(), overwrite = true)
