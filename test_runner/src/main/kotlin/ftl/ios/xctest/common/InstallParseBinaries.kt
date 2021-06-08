package ftl.ios.xctest.common

import flank.common.OutputLogLevel
import flank.common.appDataDirectory
import flank.common.createDirectoryIfNotExist
import flank.common.createLinkToFile
import flank.common.downloadFile
import flank.common.hasAllFiles
import flank.common.isMacOS
import flank.common.isWindows
import flank.common.logLn
import flank.common.unzipFile
import java.nio.file.Files
import java.nio.file.Paths

internal val installBinaries by lazy {
    if (!isMacOS) {
        downloadAndCopyBinaries()
    }
}

private fun downloadAndCopyBinaries() {
    val osName = if (isWindows) "windows" else "linux"
    (osName)
        .takeIf { shouldDownloadBinaries() }
        ?.let(::downloadAndUnzip)
}

private fun shouldDownloadBinaries(): Boolean {
    val binariesPath = flankBinariesDirectory.toFile()
    return !(binariesPath.exists() && binariesPath.isDirectory && binariesPath.hasAllFiles(neededFilesListByOs()))
}

private fun neededFilesListByOs(): List<String> = if (isWindows) {
    listOf(
        "libatomic.so.1",
        "libatomic.so.1.2.0",
        "nm.exe",
        "swift-demangle.exe",
        "swiftDemangle.dll",
        "xargs.exe",
        "msys-intl-8.dll",
        "msys-iconv-2.dll",
        "msys-2.0.dll"
    )
} else {
    listOf("nm", "swift-demangle", "libatomic.so.1", "libatomic.so.1.2.0")
}

private val flankBinariesDirectory = Paths.get(appDataDirectory, ".flank").toAbsolutePath()

private fun downloadAndUnzip(osname: String) {
    createDirectoryIfNotExist(flankBinariesDirectory)
    val destinationFile = Paths.get(flankBinariesDirectory.toString(), "binaries.zip")

    downloadFile(
        sourceUrl = "https://github.com/Flank/binaries/releases/download/$osname/binaries.zip",
        destinationPath = destinationFile
    )

    unzipFile(destinationFile.toFile().absoluteFile, flankBinariesDirectory.toString())
        .forEach {
            logLn("Binary file $it copied to $flankBinariesDirectory", OutputLogLevel.DETAILED)
            it.setExecutable(true)
        }
    Files.delete(destinationFile)
    createLinkToFile(
        link = Paths.get(flankBinariesDirectory.toString(), "libatomic.so.1"),
        target = Paths.get(flankBinariesDirectory.toString(), "libatomic.so.1.2.0")
    )
}
