package flank.scripts.shell.firebase

import com.github.ajalt.clikt.core.CliktCommand
import flank.scripts.exceptions.ShellCommandException
import flank.scripts.utils.checkIfPipInstalled
import flank.scripts.utils.installClientGeneratorIfNeeded
import flank.scripts.utils.runCommand
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

class GenerateJavaClient : CliktCommand(name = "generateJavaClient", help = "Generate Java Client") {

    override fun run() {
        checkIfPipInstalled()
        installClientGeneratorIfNeeded()
        val apiPath = Paths.get("test_api").toString()
        val outputDirectory = Paths.get(apiPath, "src", "main", "java").toString()
        val testingJsonInput = Paths.get("json", "testing_v1.json").toString()
        Paths.get(apiPath, "src").toFile().deleteRecursively()

        val generateLibraryCommand = "generate_library " +
            "--input=$testingJsonInput " +
            "--language=java " +
            "--output_dir=$outputDirectory"

        val result = generateLibraryCommand.runCommand()
        if (result != 0) throw ShellCommandException("Error when execute generate_library command")

        Files.move(
            Paths.get(outputDirectory, "pom.xml"),
            Paths.get(apiPath, "pom.xml"),
            StandardCopyOption.REPLACE_EXISTING
        )
    }
}
