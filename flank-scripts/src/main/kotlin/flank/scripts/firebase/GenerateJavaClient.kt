package flank.scripts.firebase

import com.github.ajalt.clikt.core.CliktCommand
import flank.scripts.utils.checkAndInstallIfNeed

class GenerateJavaClient : CliktCommand(name = "generateJavaClient", help = "Generate Java Client") {
    override fun run() {
        installClientGeneratorIfNeeded()
    }
}

fun installClientGeneratorIfNeeded() {
    val isWindows = System.getProperty("os.name").startsWith("Windows")
    val generateLibraryCheckCommand = (if (isWindows) "where " else "command -v ") + "generate_library"
    generateLibraryCheckCommand.checkAndInstallIfNeed("pip install google-apis-client-generator")
}


