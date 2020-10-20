package flank.scripts.shell

import com.github.ajalt.clikt.core.CliktCommand
import flank.scripts.shell.utils.createGradleCommand
import flank.scripts.shell.utils.rootDirectoryPathString
import flank.scripts.utils.runCommand
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

object BuildFlankCommand : CliktCommand(name = "buildFlank", help = "Build Flank") {
    override fun run() {
        buildFlank()
    }
}

private fun buildFlank() {
    createGradleCommand(
        workingDir = rootDirectoryPathString,
        "-p", rootDirectoryPathString, ":test_runner:clean", ":test_runner:assemble", ":test_runner:shadowJar"
    )
        .runCommand()

    copyFlankOutputFile()
}

private fun copyFlankOutputFile() {
    val flankDirectory = Paths.get(rootDirectoryPathString, "test_runner").toString()

    Files.copy(
        Paths.get(flankDirectory, "build", "libs", "flank.jar"),
        Paths.get(flankDirectory, "bash", "flank.jar"),
        StandardCopyOption.REPLACE_EXISTING
    )
}
