@file:CompilerOptions("jvmTarget = '1.8'")

@file:Import("../util/downloadFiles.main.kts")
@file:Import("../util/archive.main.kts")
@file:Import("update.constants.kts")

import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

private val currentPath = Paths.get("")
private val llvmPath = Paths.get(currentPath.toString(), "llvm")

fun updateLlvmWindows() {
    val llvmExe = Paths.get(llvmPath.toString(), "LLVM-win64.exe")
    if (llvmExe.toFile().exists()) {
        println("LLVM exists")
    } else {
        println("Downloading Windows LLVM...")
        llvmPath.toFile().mkdirs()
        downloadFile(
            url = "https://releases.llvm.org/8.0.0/LLVM-8.0.0-win64.exe",
            destinationPath = llvmExe.toString()
        )
    }

    llvmExe.toFile().extract(llvmPath.toFile(), "zip", "xz")
    findAndCopyLicense()
    findAndCopyLlvmNmFile()
    llvmPath.toFile().deleteRecursively()
}

fun updateLlvmNonWindows() {
    val llvmTarXz = Paths.get(llvmPath.toString(), "llvm.tar.xz")

    if (llvmTarXz.toFile().exists()) {
        println("LLVM exists")
    } else {
        println("Downloading LLVM for Windows...")
        llvmPath.toFile().mkdirs()
        downloadFile(
            url = "http://releases.llvm.org/8.0.0/clang+llvm-8.0.0-x86_64-linux-gnu-ubuntu-16.04.tar.xz",
            destinationPath = llvmTarXz.toString()
        )
    }

    llvmTarXz.toFile().extract(llvmPath.toFile(), "tar", "xz")
    findAndCopyLicense()
    findAndCopyLlvmNmFile()
    llvmPath.toFile().deleteRecursively()
}

fun updateLlvm() = if (isWindows) updateLlvmWindows() else updateLlvmNonWindows()

fun findAndCopyLicense() {
    val licensePathSuffix = Paths.get("include", "llvm", "Support", "LICENSE.TXT").toString()
    val licenseOutputFile = Paths.get(currentPath.toString(), "llvm.txt").toFile()

    println("Copying license ...")
    Files.walk(llvmPath)
        .filter { it.toString().endsWith(licensePathSuffix) }
        .findFirst()
        .takeIf { it.isPresent }
        ?.run { get().toFile().copyTo(licenseOutputFile, overwrite = true) }
}

fun findAndCopyLlvmNmFile() {
    val llvmNmSuffix = Paths.get("bin", "llvm-nm").toString()
    val llvmNmOutputFile = Paths.get(currentPath.toString(), "nm").toFile()

    println("Copying llvm nm ...")
    Files.walk(llvmPath)
        .filter { it.toString().endsWith(llvmNmSuffix) }
        .findFirst()
        .takeIf { it.isPresent }
        ?.run { get().toFile().copyTo(llvmNmOutputFile, overwrite = true) }
}
