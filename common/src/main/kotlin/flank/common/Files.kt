package flank.common

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.awaitUnit
import com.github.kittinunf.fuel.httpDownload
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

val defaultCredentialPath: Path by lazy {
    Paths.get(userHome, ".config/gcloud/application_default_credentials.json")
}

val userHome: String by lazy {
    if (isWindows) System.getenv("HOMEPATH") else System.getProperty("user.home")
}

val appDataDirectory: String by lazy {
    if (isWindows) System.getenv("APPDATA") else System.getProperty("user.home")
}

val dotFlank = Paths.get(userHome, ".flank")

fun linkFiles(
    link: String,
    target: String
) {
    if (isWindows) createCopy(target, link)
    else createSymbolicLink(link, target)
}

fun createCopy(sourceDirectoryLocation: String, destinationDirectoryLocation: String) {
    if (destinationDirectoryLocation.fileExists()) {
        deleteDirectory(destinationDirectoryLocation)
    }
    copyDirectory(sourceDirectoryLocation, destinationDirectoryLocation)
}

fun createFileCopy(source: String, destination: String): Path =
    Files.copy(Paths.get(source), Paths.get(destination), StandardCopyOption.REPLACE_EXISTING)

fun copyDirectory(sourceDirectoryLocation: String, destinationDirectoryLocation: String) {
    Files.walk(Paths.get(sourceDirectoryLocation))
        .forEach { source: Path ->
            val destination =
                Paths.get(destinationDirectoryLocation, source.toString().substring(sourceDirectoryLocation.length))
            try {
                Files.copy(source, destination)
            } catch (e: IOException) {
                logLn(e.localizedMessage)
            }
        }
}

fun deleteDirectory(directory: String) {
    if (directory.fileExists()) deleteDirectory(directory.toFile())
}

private fun deleteDirectory(directory: File): Boolean {
    val allContents = directory.listFiles()
    if (allContents != null) {
        for (file in allContents) {
            deleteDirectory(file)
        }
    }
    return directory.delete()
}

fun createSymbolicLink(
    link: String,
    target: String
): Path = Files.createSymbolicLink(
    Paths.get(link).also { linkPath -> if (Files.isSymbolicLink(linkPath)) Files.delete(linkPath) }.toAbsolutePath()
        .normalize(),
    Paths.get(target).toAbsolutePath().normalize()
)

fun createLinkToFile(link: Path, target: Path): Path =
    if (isWindows) createFileCopy(target.toString(), link.toString())
    else Files.createSymbolicLink(link, target.fileName)

fun downloadFile(sourceUrl: String, destination: String) {
    Fuel.download(sourceUrl)
        .fileDestination { _, _ -> File(destination) }
        .responseString()
}

suspend fun String.downloadFile(destination: String) = this.httpDownload().fileDestination { _, _ ->
    Files.createFile(Paths.get(destination)).toFile().also {
        logLn("Ktlint written to: ${it.absolutePath}")
    }
}.awaitUnit()

fun downloadFile(sourceUrl: String, destinationPath: Path) {
    Fuel.download(sourceUrl)
        .fileDestination { _, _ -> destinationPath.toFile() }
        .responseString()
}

fun String.toFile(): File = Paths.get(this).toFile()

fun String.deleteFile() = Paths.get(this).delete()

fun createDirectoryIfNotExist(path: Path) {
    if (Files.notExists(path)) Files.createDirectory(path)
}

fun File.hasAllFiles(fileList: List<String>): Boolean {
    val directoryFiles = list() ?: emptyArray()
    return fileList.all { it in directoryFiles }
}

fun String.fileExists(): Boolean = Paths.get(this).exists()

fun osPathSeparator() = (if (isWindows) "\\" else "/")

private fun Path.exists(): Boolean = Files.exists(this)

private fun Path.isFile(): Boolean = !Files.isDirectory(this)

private fun Path.delete(): Boolean {
    return if (isFile() && exists()) {
        Files.delete(this)
        true
    } else {
        false
    }
}
