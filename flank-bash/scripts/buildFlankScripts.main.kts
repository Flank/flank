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

val flankScriptsDirectory: Path = Paths.get(rootDirectoryPathString, "flank-scripts")

shell {
    shell {
        createGradleCommand(
            workingDir = rootDirectoryPathString,
            options = ":flank-scripts:clean", ":flank-scripts:assemble", ":flank-scripts:shadowJar")()
    }
}

Paths.get(flankScriptsDirectory.toString(), "build", "libs", "flankScripts.jar").toFile()
    .copyTo(Paths.get(flankScriptsDirectory.toString(), "bash", "flankScripts.jar").toFile(), overwrite = true)
