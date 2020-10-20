package flank.scripts.shell.ops

import com.github.ajalt.clikt.core.CliktCommand
import flank.scripts.shell.utils.flankFixturesTmpPath
import flank.scripts.shell.utils.testProjectsPath
import flank.scripts.utils.runCommand
import java.nio.file.Path
import java.nio.file.Paths

object GoOpsCommand : CliktCommand(name = "buildGoApps", help = "build go sample apps") {
    override fun run() {
        generateGoArtifacts()
    }

    private fun generateGoArtifacts() {
        val goHelloBinDirectoryPath = Paths.get(testProjectsPath, "gohello", "bin").apply {
            toFile().deleteRecursively()
        }
        GoOS.values().forEach { createExecutable(it, goHelloBinDirectoryPath) }
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
}
