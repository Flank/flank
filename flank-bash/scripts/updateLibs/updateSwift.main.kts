@file:CompilerOptions("jvmTarget = '1.8'")

@file:Import("../util/downloadFiles.main.kts")
@file:Import("../util/unpack.main.kts")
@file:Import("update.constants.kts")

import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

private val currentPath = Paths.get("")
private val swiftPath = Paths.get(currentPath.toString(), "swift")

fun updateSwiftWindows() {
    val swiftExe = Paths.get(swiftPath.toString(), "swift.exe")

    if (swiftExe.toFile().exists()) {
        println("Swift exists")
    } else {
        println("Downloading swift")
        swiftPath.toFile().mkdirs()
        downloadFile(
            url = "https://swift.org/builds/swift-5.3-release/windows10/swift-5.3-RELEASE/swift-5.3-RELEASE-windows10.exe",
            destinationPath = swiftExe.toString()
        )
    }

    swiftExe.toFile().extract(swiftPath.toFile(), "zip", "gz")
    findAndCopyLicense()
    findAndCopySwiftDemangleFile()
    swiftPath.toFile().deleteRecursively()
}

fun updateSwiftOther() {
    val swiftTarGz = Paths.get(swiftPath.toString(), "swift.tar.gz")

    if (swiftTarGz.toFile().exists()) {
        println("Swift exists")
    } else {
        println("Downloading swift")
        swiftPath.toFile().mkdirs()
        downloadFile(
            url = "https://swift.org/builds/swift-5.0.1-release/ubuntu1604/swift-5.0.1-RELEASE/swift-5.0.1-RELEASE-ubuntu16.04.tar.gz",
            destinationPath = swiftTarGz.toString()
        )
    }

    swiftTarGz.toFile().extract(swiftPath.toFile(), "tar", "gz")
    findAndCopyLicense()
    findAndCopySwiftDemangleFile()
    swiftPath.toFile().deleteRecursively()
}

fun updateSwift() = if (isWindows) updateSwiftWindows() else updateSwiftOther()

fun findAndCopyLicense() {
    val licenseFileSuffix = Paths.get("usr", "share", "swift", "LICENSE.txt").toString()
    val licenseOutputFile = Paths.get(currentPath.toString(), "swift.txt").toFile()

    println("Copying license ...")
    Files.walk(swiftPath)
        .filter { it.toString().endsWith(licenseFileSuffix) }
        .findFirst()
        .takeIf { it.isPresent }
        ?.run { get().toFile().copyTo(licenseOutputFile, overwrite = true) }
}

fun findAndCopySwiftDemangleFile() {
    val switftDemangleFileSuffix = Paths.get("usr", "bin", "swift-demangle").toString()
    val switftDemangleOutputFile = Paths.get(currentPath.toString(), "swift-demangle").toFile()

    println("Copying swift-demangle ...")
    Files.walk(swiftPath)
        .filter { it.toString().endsWith(switftDemangleFileSuffix) }
        .findFirst()
        .takeIf { it.isPresent }
        ?.run { get().toFile().copyTo(switftDemangleOutputFile, overwrite = true) }
}
