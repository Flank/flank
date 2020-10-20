package flank.scripts.shell.ios

import com.github.ajalt.clikt.core.CliktCommand
import flank.scripts.shell.utils.currentPath
import flank.scripts.utils.runCommand
import java.nio.file.Paths

object UniversalFrameworkCommand : CliktCommand(name = "iosUniversalFramework", help = "??") {
    override fun run() {
        failIfWindows()
        createUniversalFiles()
    }
}

private const val APP_FRAMEWORK_FRAMEWORK = "AppFramework.framework"
private const val APP_FRAMEWORK = "AppFramework"

private fun createUniversalFiles() {
    val comboPath = Paths.get(currentPath.toString(), "ios-frameworks").toString()
    val devicePath = Paths.get(comboPath, "Debug-iphoneos").toString()
    val simPath = Paths.get(comboPath, "Debug-iphonesimulator").toString()

    listOf(
        "libChannelLib.a",
        "libCommonLib.a",
        "libeDistantObject.a",
        "libTestLib.a",
        "libUILib.a"
    ).map { fileName ->
        createLipoCommand(
            outputPath = Paths.get(comboPath, fileName).toString(),
            Paths.get(devicePath, fileName).toString(), Paths.get(simPath, fileName).toString()
        )
    }.forEach { command -> command.runCommand() }

    copyAppFrameworkFiles(devicePath, comboPath)

    runDsym(
        universalFileOutput = Paths.get(comboPath, APP_FRAMEWORK_FRAMEWORK, APP_FRAMEWORK).toString(),
        comboPath = comboPath,
        files = arrayOf(
            Paths.get(devicePath, APP_FRAMEWORK_FRAMEWORK, APP_FRAMEWORK).toString(),
            Paths.get(simPath, APP_FRAMEWORK_FRAMEWORK, APP_FRAMEWORK).toString()
        )
    )
}

private fun copyAppFrameworkFiles(fromPath: String, toPath: String) {
    Paths.get(fromPath, APP_FRAMEWORK_FRAMEWORK).toFile()
        .copyRecursively(Paths.get(toPath, APP_FRAMEWORK_FRAMEWORK).toFile(), overwrite = true)
}

private fun runDsym(
    universalFileOutput: String,
    comboPath: String,
    files: Array<String>
) {
    createLipoCommand(universalFileOutput, *files).runCommand()
    "dsymutil $universalFileOutput --out ${Paths.get(comboPath, "AppFramework.framework.dSYM")}".runCommand()
}
