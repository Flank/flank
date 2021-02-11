package flank.scripts.ops.firebase

import flank.scripts.utils.checkAndInstallIfNeed
import flank.scripts.utils.commandInstalledOr
import flank.scripts.utils.exceptions.ShellCommandException
import flank.scripts.utils.runCommand
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

fun generateJavaClient() {
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

private fun checkIfPipInstalled() {
    "pip".commandInstalledOr {
        println("You need pip fot this script. To install it follow https://pip.pypa.io/en/stable/installing/")
    }
}

private fun installClientGeneratorIfNeeded() {
    "generate_library".checkAndInstallIfNeed("pip install google-apis-client-generator")
}
