@file:Repository("https://dl.bintray.com/jakubriegel/kotlin-shell")
@file:DependsOn("eu.jrie.jetbrains:kotlin-shell-core:0.2.1")
@file:DependsOn("org.slf4j:slf4j-simple:1.7.28")

@file:Import("util/GradleCommand.kt")
@file:Import("util/PathHelper.kt")

@file:CompilerOptions("-Xopt-in=kotlin.RequiresOptIn")
@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

import eu.jrie.jetbrains.kotlinshell.shell.*
import java.nio.file.Path
import java.nio.file.Paths

val flankDirectory: Path = Paths.get(rootDirectoryPathString, "test_runner")

shell {
    shell(dir = rootDirectoryFile) {
        createGradleCommand(":test_runner:clean", ":test_runner:assemble", ":test_runner:shadowJar")()
    }
}

Paths.get(flankDirectory.toString(), "build", "libs", "flank.jar").toFile()
    .copyTo(Paths.get(flankDirectory.toString(), "bash", "flank.jar").toFile(), overwrite = true)
