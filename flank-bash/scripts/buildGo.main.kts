#!/usr/bin/env kotlin

@file:Repository("https://dl.bintray.com/jakubriegel/kotlin-shell")
@file:DependsOn("eu.jrie.jetbrains:kotlin-shell-core:0.2.1")
@file:DependsOn("org.slf4j:slf4j-simple:1.7.28")

@file:Import("PathHelper.kt")

@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

import java.nio.file.Paths
import eu.jrie.jetbrains.kotlinshell.shell.*

enum class GoOS(
    val goName: String,
    val directory: String,
    val extension: String = ""
) {
    LINUX("linux", "bin/linux"),
    MAC("darwin", "bin/mac"),
    WINDOWS("windows", "bin/win", ".exe"),
}

private val goHelloDirectory =
    (if (args.isNotEmpty()) Paths.get(args[1]) else Paths.get(rootDirectoryPath, "test_project", "gohello"))
        .toString()
private val goHelloBinDirectoryPath = Paths.get(goHelloDirectory, "bin")

fun createExecutable(os: GoOS) {
    Paths.get(goHelloBinDirectoryPath.toString(), *os.directory.split('/').toTypedArray())
        .toFile()
        .mkdirs()
    shell {
        "GOOS=${os.goName}"()
        "GOARCH=amd64"()
        "go build -o ./${os.directory}/gohello${os.extension}"()
    }
}

goHelloBinDirectoryPath.toFile().deleteRecursively()

GoOS.values().forEach { createExecutable(it) }

