package flank.scripts.ops.shell.buildexample.go

import flank.common.flankFixturesTmpPath
import flank.common.testProjectsPath
import flank.scripts.utils.runCommand
import java.nio.file.Path
import java.nio.file.Paths

fun generateGoArtifacts() {
    val goHelloBinDirectoryPath = Paths.get(testProjectsPath, "gohello", "bin").apply {
        toFile().deleteRecursively()
    }
    GoOS.values().forEach { createExecutable(it, goHelloBinDirectoryPath) }
}

enum class GoOS(
    val goName: String,
    val directory: String,
    val extension: String = ""
) {
    LINUX("linux", "bin/linux"),
    MAC("darwin", "bin/mac"),
    WINDOWS("windows", "bin/win", ".exe"),
}

private fun createExecutable(os: GoOS, goHelloBinDirectoryPath: Path) {
    Paths.get(goHelloBinDirectoryPath.toString(), *os.directory.split('/').toTypedArray())
        .toFile()
        .mkdirs()
    "go build -o ${os.createOutputPathForBinary()}".runCommand(
        environmentVariables = mapOf(
            "GOOS" to os.goName,
            "GOARCH" to "amd64"
        )
    )
}

private fun GoOS.createOutputPathForBinary() =
    Paths.get(flankFixturesTmpPath, "gohello", directory, "gohello$extension")
