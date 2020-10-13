
@file:Repository("https://dl.bintray.com/jakubriegel/kotlin-shell")
@file:DependsOn("eu.jrie.jetbrains:kotlin-shell-core:0.2.1")
@file:DependsOn("org.slf4j:slf4j-simple:1.7.28")

@file:CompilerOptions("-Xopt-in=kotlin.RequiresOptIn")
@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

@file:Import("../PathHelper.kt")

import eu.jrie.jetbrains.kotlinshell.shell.shell
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.system.exitProcess

val id = args.firstOrNull() ?: {
    println("Pass device id. Please take it from Xcode -> Window -> Devices and Simulators")
    exitProcess(1)
}

val dataPath: Path = Paths.get(currentPath.toString(), "dd_tmp", "Build" , "Products")

val xcodeCommand = "xcodebuild test-without-building " +
    " -xctestrun $dataPath/*.xctestrun " +
    "-derivedDataPath $dataPath " +
    "-destination 'id=$id'"

shell {
    xcodeCommand()
}
