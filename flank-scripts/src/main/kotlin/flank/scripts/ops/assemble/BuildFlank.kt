package flank.scripts.ops.assemble

import flank.common.rootDirectoryPathString
import flank.scripts.utils.createGradleCommand
import flank.scripts.utils.runCommand
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

fun buildFlank(clean: Boolean = true) {
    createGradleCommand(
        workingDir = rootDirectoryPathString,
        "-p", rootDirectoryPathString,
        ":test_runner:clean".takeIf { clean },
        ":test_runner:assemble",
        ":test_runner:shadowJar"
    ).runCommand()
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
