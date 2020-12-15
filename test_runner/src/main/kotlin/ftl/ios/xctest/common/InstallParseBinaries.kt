package ftl.ios.xctest.common

import ftl.config.FtlConstants
import ftl.config.FtlConstants.isMacOS
import ftl.config.FtlConstants.isWindows
import ftl.log.OutputLogLevel
import ftl.log.logLn
import ftl.util.createDirectoryIfNotExist
import ftl.util.createSymbolicLink
import ftl.util.download
import ftl.util.hasAllFiles
import ftl.util.unzipFile
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
    listOf("libatomic.so.1", "libatomic.so.1.2.0") // more files should be added after #1134"
} else {
    listOf("nm", "swift-demangle", "libatomic.so.1", "libatomic.so.1.2.0")
}

private val flankBinariesDirectory = Paths.get(FtlConstants.userHome, ".flank")

private fun downloadAndUnzip(osname: String) {
    createDirectoryIfNotExist(flankBinariesDirectory)
    val destinationFile = Paths.get(flankBinariesDirectory.toString(), "binaries.zip")

    download(
        sourceUrl = "https://github.com/Flank/binaries/releases/download/$osname/binaries.zip",
        destination = destinationFile
    )
    createDirectoryIfNotExist(flankBinariesDirectory)
    unzipFile(destinationFile.toFile().absoluteFile, flankBinariesDirectory.toString())
        .forEach {
            logLn("Binary file $it copied to $flankBinariesDirectory", OutputLogLevel.DETAILED)
            it.setExecutable(true)
        }
    Files.delete(destinationFile)
    createSymbolicLink(
        link = Paths.get(flankBinariesDirectory.toString(), "libatomic.so.1"),
        target = Paths.get(flankBinariesDirectory.toString(), "libatomic.so.1.2.0")
    )
}
