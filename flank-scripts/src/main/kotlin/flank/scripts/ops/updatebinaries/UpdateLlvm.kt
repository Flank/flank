package flank.scripts.ops.updatebinaries

import flank.common.downloadFile
import flank.common.extract
import flank.scripts.utils.isWindows
import java.nio.file.Files
import java.nio.file.Paths

private val currentPath = Paths.get("")
private val llvmPath =
    if (isWindows) Paths.get(currentPath.toString(), "master")
    else Paths.get(currentPath.toString(), "llvm")

fun updateLlvm() = if (isWindows) retrieveNMForWindows() else updateLlvmNonWindows()

private fun retrieveNMForWindows() {
    val binariesPath = Paths.get(currentPath.toString(), "master.zip")
    if (binariesPath.toFile().exists()) {
        println("Binaries already exists")
    } else {
        println("Downloading binaries for windows...")
        downloadFile(
            sourceUrl = "https://github.com/Flank/binaries/archive/master.zip",
            destination = binariesPath.toString()
        )
    }
    val destinationPath = Paths.get(currentPath.toString(), "master")
    destinationPath.toFile().mkdirs()
    binariesPath.toFile().extract(destinationPath.toFile(), "zip")
    findAndCopyLlvmNmFile()
    llvmPath.toFile().deleteRecursively()
}

private fun updateLlvmNonWindows() {
    val llvmTarXz = Paths.get(llvmPath.toString(), "llvm.tar.xz")

    if (llvmTarXz.toFile().exists()) {
        println("LLVM exists")
    } else {
        println("Downloading LLVM...")
        llvmPath.toFile().mkdirs()
        downloadFile(
            sourceUrl = "http://releases.llvm.org/8.0.0/clang+llvm-8.0.0-x86_64-linux-gnu-ubuntu-16.04.tar.xz",
            destination = llvmTarXz.toString()
        )
    }

    llvmTarXz.toFile().extract(llvmPath.toFile(), "tar", "xz")
    findAndCopyLlvmLicense()
    findAndCopyLlvmNmFile()
    llvmPath.toFile().deleteRecursively()
}

private fun findAndCopyLlvmLicense() {
    val licensePathSuffix = Paths.get("include", "llvm", "Support", "LICENSE.TXT").toString()
    val licenseOutputFile = Paths.get(currentPath.toString(), "llvm.txt").toFile()

    println("Copying license ...")
    Files.walk(llvmPath)
        .filter { it.toString().endsWith(licensePathSuffix) }
        .findFirst()
        .takeIf { it.isPresent }
        ?.run { get().toFile().copyTo(licenseOutputFile, overwrite = true) }
}

private fun findAndCopyLlvmNmFile() {
    val llvmNmSuffix =
        if (!isWindows) Paths.get("bin", "llvm-nm").toString()
        else Paths.get("master", "binaries-master", "llvm-nm.exe").toString()

    val llvmNmOutputFile =
        if (!isWindows) Paths.get(currentPath.toString(), "nm").toFile()
        else Paths.get(currentPath.toString(), "nm.exe").toFile()

    println("Copying llvm nm ...")
    Files.walk(llvmPath)
        .filter { it.toString().endsWith(llvmNmSuffix) }
        .findFirst()
        .takeIf { it.isPresent }
        ?.run { get().toFile().copyTo(llvmNmOutputFile, overwrite = true) }
}
