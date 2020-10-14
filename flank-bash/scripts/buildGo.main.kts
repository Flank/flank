#!/usr/bin/env kotlin

@file:Repository("https://dl.bintray.com/jakubriegel/kotlin-shell")
@file:DependsOn("eu.jrie.jetbrains:kotlin-shell-core:0.2.1")
@file:DependsOn("org.slf4j:slf4j-simple:1.7.28")

@file:Import("util/PathHelper.kt")

@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

import eu.jrie.jetbrains.kotlinshell.processes.process.ProcessSendChannel
import java.nio.file.Paths
import eu.jrie.jetbrains.kotlinshell.shell.*
import java.io.File

enum class GoOS(
    val goName: String,
    val directory: String,
    val extension: String = ""
) {
    LINUX("linux", "bin/linux"),
    MAC("darwin", "bin/mac"),
    WINDOWS("windows", "bin/win", ".exe"),
}

val goHelloDirectory =
    (if (args.isNotEmpty()) Paths.get(args.first()) else Paths.get(rootDirectoryPath, "test_projects", "gohello"))
        .toString()
private val goHelloBinDirectoryPath = Paths.get(goHelloDirectory, "bin")

suspend fun createExecutable(os: GoOS) {
    Paths.get(goHelloBinDirectoryPath.toString(), *os.directory.split('/').toTypedArray())
        .toFile()
        .mkdirs()

    shell(dir = Paths.get(rootDirectoryPath, "test_projects", "gohello").toFile()) {
        export("GOOS" to os.goName)
        export("GOARCH" to "amd64")
        "go build -o $flankFixturesTmpPath/gohello/${os.directory}/gohello${os.extension}"()
    }

}

suspend fun generateGoArtifacts() {
    goHelloBinDirectoryPath.toFile().deleteRecursively()
    GoOS.values().forEach { createExecutable(it) }
}


