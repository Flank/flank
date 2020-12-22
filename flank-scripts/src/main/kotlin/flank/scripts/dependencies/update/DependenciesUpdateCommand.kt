package flank.scripts.dependencies.update

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import java.io.File

object DependenciesUpdateCommand : CliktCommand(name = "update", help = "Update dependencies") {

    private val reportFile
        by option(help = "Path to .json report file")
        .file(mustExist = true)
        .default(File("./report.json"))
    private val dependenciesFile
        by option(help = "Path to .kts file with dependencies defined")
        .file(mustExist = true)
        .default(File("./buildSrc/src/main/kotlin/Dependencies.kt"))
    private val versionsFile
        by option(help = "Path to .kts file with versions defined")
        .file(mustExist = true)
        .default(File("./buildSrc/src/main/kotlin/Versions.kt"))
    private val pluginsFile
        by option(help = "Path to .kts file with plugins defined")
        .file(mustExist = true)
        .default(File("./buildSrc/src/main/kotlin/Plugins.kt"))

    override fun run() {
        reportFile.updateDependencies(
            dependenciesFile = dependenciesFile,
            versionsFile = versionsFile
        )
        reportFile.updateGradle()
        reportFile.updatePlugins(pluginsFile, versionsFile)
    }
}
