@file:Repository("https://dl.bintray.com/jakubriegel/kotlin-shell")
@file:DependsOn("eu.jrie.jetbrains:kotlin-shell-core:0.2.1")
@file:DependsOn("org.slf4j:slf4j-simple:1.7.28")
@file:Import("GradleCommand.kt")
@file:Import("PathHelper.kt")
@file:CompilerOptions("-Xopt-in=kotlin.RequiresOptIn")
@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import eu.jrie.jetbrains.kotlinshell.shell.*
import java.nio.file.Path
import java.nio.file.StandardCopyOption

fun Shell.generateIos() = takeUnless { isWindows }?.let {

}
