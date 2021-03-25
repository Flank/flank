package flank.scripts.ops.updatebinaries

import flank.common.downloadFile
import flank.common.extract
import flank.common.isWindows
import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Collectors

private val currentPath = Paths.get("")
private val atomicPath = Paths.get(currentPath.toString(), "libatomic")

fun updateAtomic() {
    if (isWindows) return

    val atomicDeb = Paths.get(atomicPath.toString(), "libatomic.deb").toFile()
    val atomicDataTarXz = Paths.get(atomicPath.toString(), "data.tar.xz").toFile()

    if (atomicDeb.exists()) {
        println("Atomic exists")
    } else {
        println("Downloading Atomic...")
        atomicPath.toFile().mkdirs()
        downloadFile(
            sourceUrl = "http://mirrors.kernel.org/ubuntu/pool/main/g/gcc-8/libatomic1_8-20180414-1ubuntu2_amd64.deb",
            destination = atomicDeb.toString()
        )
    }

    atomicDeb.extract(atomicPath.toFile(), "ar")
    atomicDataTarXz.extract(atomicPath.toFile(), "tar", "xz")
    findAndCopyAtomicLicense()
    findAndCopyAtomicFiles()
    atomicPath.toFile().deleteRecursively()
}

private fun findAndCopyAtomicLicense() {
    val licenseOutputFile = Paths.get(currentPath.toString(), "libatomic.txt").toFile()

    downloadFile(
        "http://changelogs.ubuntu.com/changelogs/pool/main/g/gcc-8/gcc-8_8-20180414-1ubuntu2/copyright",
        licenseOutputFile.toString()
    )
}

private fun findAndCopyAtomicFiles() {
    println("Copying atomic files ...")
    val list = Files.walk(atomicPath)
        .filter { it.toString().endsWith("libatomic.so.1") || it.toString().endsWith("libatomic.so.1.2.0") }
        .collect(Collectors.toList())

    list.forEach {
        it.toFile().copyTo(Paths.get(currentPath.toString(), it.fileName.toString()).toFile(), true)
    }
}
