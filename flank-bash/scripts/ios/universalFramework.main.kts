@file:Repository("https://dl.bintray.com/jakubriegel/kotlin-shell")
@file:DependsOn("eu.jrie.jetbrains:kotlin-shell-core:0.2.1")
@file:DependsOn("org.slf4j:slf4j-simple:1.7.28")

@file:CompilerOptions("-Xopt-in=kotlin.RequiresOptIn")
@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

@file:Import("LipoHelper.kt")
@file:Import("../PathHelper.kt")

import eu.jrie.jetbrains.kotlinshell.shell.shell
import java.nio.file.Paths

val comboPath = Paths.get(currentPath.toString(), "ios-frameworks").toString()
val devicePath = Paths.get(comboPath, "Debug-iphoneos").toString()
val simPath = Paths.get(comboPath, "Debug-iphonesimulator").toString()

val universalFilesNames = listOf(
    "libChannelLib.a",
    "libCommonLib.a",
    "libeDistantObject.a",
    "libTestLib.a",
    "libUILib.a"
)

shell {
    universalFilesNames.forEach {fileName ->
        createLipoCommand(
            outputPath = Paths.get(comboPath, fileName).toString(),
            Paths.get(devicePath, fileName).toString(), Paths.get(simPath, fileName).toString()
        )
    }
}
private val appFrameworkFramework = "AppFramework.framework"
private val appFramework = "AppFramework"
Paths.get(devicePath, appFrameworkFramework).toFile()
    .copyRecursively(Paths.get(comboPath, appFrameworkFramework).toFile(), overwrite = true)

val deviceFramework = Paths.get(devicePath, appFrameworkFramework, appFramework).toString()
val simFramework = Paths.get(simPath, appFrameworkFramework, appFramework).toString()
val universalFramework = Paths.get(comboPath, appFrameworkFramework, appFramework).toString()

shell {
    createLipoCommand(
        outputPath = universalFramework,
        deviceFramework, simFramework
    )

    "dsymutil $universalFramework --out ${Paths.get(comboPath, "AppFramework.framework.dSYM")}"
}
