package flank.scripts.shell

import com.github.ajalt.clikt.core.CliktCommand
import flank.scripts.shell.utils.createGradleCommand
import flank.scripts.shell.utils.rootDirectoryPathString
import java.nio.file.Paths

object BuildFlankCommand : CliktCommand(name = "buildFlank", help = "Build Flank") {
    override fun run() {
        buildFlank()
    }
}

private fun buildFlank() {
    createGradleCommand(
        workingDir = rootDirectoryPathString,
        "-p", rootDirectoryPathString, ":test_runner:clean", ":test_runner:assemble", ":test_runner:shadowJar")

    copyFlankOutputFile()
}

private fun copyFlankOutputFile() {
    val flankDirectory = Paths.get(rootDirectoryPathString, "test_runner").toString()

    Paths.get(flankDirectory, "build", "libs", "flank.jar").toFile()
        .copyTo(Paths.get(flankDirectory, "bash", "flank.jar").toFile(), overwrite = true)
}
