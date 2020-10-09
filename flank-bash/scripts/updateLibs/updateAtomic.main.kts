@file:CompilerOptions("jvmTarget = '1.8'")

@file:Import("downloadFiles.main.kts")
@file:Import("unpack.main.kts")

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.stream.Collectors

private val currentPath = Paths.get("")
private val atomicPath = Paths.get(currentPath.toString(), "libatomic")

fun updateAtomic() {
    val atomicDeb = Paths.get(atomicPath.toString(), "libatomic.deb").toFile()
    val atomicDataTarXz = Paths.get(atomicPath.toString(), "data.tar.xz").toFile()

    if (atomicDeb.exists()) {
        println("Atomic exists")
    } else {
        println("Downloading Atomic...")
        atomicPath.toFile().mkdirs()
        downloadFile(
            url = "http://mirrors.kernel.org/ubuntu/pool/main/g/gcc-8/libatomic1_8-20180414-1ubuntu2_amd64.deb",
            destinationPath = atomicDeb.toString()
        )
    }

    atomicDeb.extract(atomicPath.toFile(), "ar")
    atomicDataTarXz.extract(atomicPath.toFile(), "tar", "xz")
    findAndCopyLicense()
    findAndCopyAtomicFiles()
    atomicPath.toFile().deleteRecursively()
}

fun findAndCopyLicense() {
    val licenseOutputFile = Paths.get(currentPath.toString(), "libatomic.txt").toFile()

    downloadFile(
        "http://changelogs.ubuntu.com/changelogs/pool/main/g/gcc-8/gcc-8_8-20180414-1ubuntu2/copyright",
        licenseOutputFile.toString()
    )
}

fun findAndCopyAtomicFiles() {
    println("Copying atomic files ...")
    val list = Files.walk(atomicPath)
        .filter { it.toString().endsWith("libatomic.so.1") || it.toString().endsWith("libatomic.so.1.2.0") }
        .collect(Collectors.toList())

    list.forEach {
        it.toFile().copyTo(Paths.get(currentPath.toString(), it.fileName.toString()).toFile(), true)
    }
}
