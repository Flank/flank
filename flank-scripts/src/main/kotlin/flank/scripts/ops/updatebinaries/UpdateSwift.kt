package flank.scripts.ops.updatebinaries

import flank.common.downloadFile
import flank.common.extract
import flank.scripts.utils.isWindows
import java.nio.file.Files
import java.nio.file.Paths

private val currentPath = Paths.get("")
private val swiftPath = Paths.get(currentPath.toString(), "swift")

fun updateSwift() = if (isWindows) updateSwiftWindows() else updateSwiftOther()

private fun updateSwiftWindows() {
    val binariesPath = Paths.get(currentPath.toString(), "master.zip")
    if (binariesPath.toFile().exists()) {
        println("Binaries already exists")
    } else {
        println("Downloading binaries for windows...")
        binariesPath.toFile().mkdirs()
        downloadFile(
            sourceUrl = "https://github.com/Flank/binaries/archive/master.zip",
            destination = binariesPath.toString()
        )
    }

    binariesPath.toFile().extract(binariesPath.toFile(), "zip", "xz")
    findAndCopySwiftDemangleFile()
    swiftPath.toFile().deleteRecursively()
}

private fun updateSwiftOther() {
    val swiftTarGz = Paths.get(swiftPath.toString(), "swift.tar.gz")

    if (swiftTarGz.toFile().exists()) {
        println("Swift exists")
    } else {
        println("Downloading swift")
        swiftPath.toFile().mkdirs()
        downloadFile(
            sourceUrl = "https://swift.org/builds/swift-5.0.1-release/ubuntu1604/swift-5.0.1-RELEASE/swift-5.0.1-RELEASE-ubuntu16.04.tar.gz",
            destination = swiftTarGz.toString()
        )
    }

    swiftTarGz.toFile().extract(swiftPath.toFile(), "tar", "gz")
    findAndCopySwiftLicense()
    findAndCopySwiftDemangleFile()
    swiftPath.toFile().deleteRecursively()
}

private fun findAndCopySwiftLicense() {
    val licenseFileSuffix = Paths.get("usr", "share", "swift", "LICENSE.txt").toString()
    val licenseOutputFile = Paths.get(currentPath.toString(), "swift.txt").toFile()

    println("Copying license ...")
    Files.walk(swiftPath)
        .filter { it.toString().endsWith(licenseFileSuffix) }
        .findFirst()
        .takeIf { it.isPresent }
        ?.run { get().toFile().copyTo(licenseOutputFile, overwrite = true) }
}

private fun findAndCopySwiftDemangleFile() {
    val switftDemangleFileSuffix = if (isWindows) Paths.get("swift-demangle.exe").toString() else Paths.get("usr", "bin", "swift-demangle").toString()
    val switftDemangleOutputFile = if (isWindows) Paths.get(currentPath.toString(), "swift-demangle.exe").toFile() else Paths.get(currentPath.toString(), "swift-demangle").toFile()

    println("Copying swift-demangle ...")
    Files.walk(swiftPath)
        .filter { it.toString().endsWith(switftDemangleFileSuffix) }
        .findFirst()
        .takeIf { it.isPresent }
        ?.run { get().toFile().copyTo(switftDemangleOutputFile, overwrite = true) }
}
