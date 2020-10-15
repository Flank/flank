@file:Repository("https://dl.bintray.com/jakubriegel/kotlin-shell")
@file:DependsOn("eu.jrie.jetbrains:kotlin-shell-core:0.2.1")
@file:DependsOn("org.slf4j:slf4j-simple:1.7.28")

@file:CompilerOptions("-Xopt-in=kotlin.RequiresOptIn")
@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

@file:Import("../util/downloadSoftware.main.kts")
@file:Import("../util/archive.main.kts")
@file:Import("../util/PathHelper.kt")
@file:Import("IosBuildCommand.kt")

import eu.jrie.jetbrains.kotlinshell.shell.shell
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.stream.Collectors

val currentPath = Paths.get("")
val dataPath: Path = Paths.get("", "dd_tmp")
dataPath.toFile().deleteRecursively()
val archiveFileName = "earlgrey_example.zip"
val buildProductPath = Paths.get(dataPath.toString(), "Build", "Products")
downloadXcPrettyIfNeeded()

shell {
    val xcodeCommand = createIosBuildCommand(dataPath, "./EarlGreyExample.xcworkspace", "\"EarlGreyExampleSwiftTests\"").process()
    val xcPrettyCommand = "xcpretty".process()

    pipeline { xcodeCommand pipe xcPrettyCommand }

    Files.walk(Paths.get(""))
        .filter { it.fileName.toString().endsWith("-iphoneos") || it.fileName.toString().endsWith(".xctestrun") }
        .map { it.toFile() }
        .collect(Collectors.toList())
        .archive(archiveFileName, currentPath.toFile())

    Files.move(
        Paths.get("", archiveFileName),
        Paths.get(buildProductPath.toString(), archiveFileName),
        StandardCopyOption.REPLACE_EXISTING
    )
}
