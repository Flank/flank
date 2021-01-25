package flank.scripts.shell.firebase.apiclient

import com.github.ajalt.clikt.core.CliktCommand
import flank.scripts.utils.checkIfPipInstalled
import flank.scripts.utils.exceptions.ShellCommandException
import flank.scripts.utils.installClientGeneratorIfNeeded
import flank.scripts.utils.runCommand
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

object GenerateJavaClientCommand : CliktCommand(name = "generateJavaClient", help = "Generate Java Client") {

    override fun run() {
        checkIfPipInstalled()
        installClientGeneratorIfNeeded()
        val firebaseApiPath = Paths.get("firebase_apis").toString()
        val apiPath = Paths.get(firebaseApiPath, "test_api").toString()
        val outputDirectory = Paths.get(apiPath, "src", "main", "java").toString()
        val testingJsonInput = Paths.get(firebaseApiPath, "json", "testing_v1.json").toString()
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
