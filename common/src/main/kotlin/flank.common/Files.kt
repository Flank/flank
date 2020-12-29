package flank.common

import com.github.kittinunf.fuel.Fuel
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

val userHome: String by lazy {
    if (isWindows) System.getenv("HOMEPATH") else System.getProperty("user.home")
}

fun String.deleteFile() = Paths.get(this).delete()

fun createSymbolicLink(
    link: String,
    target: String
) {
    Files.createSymbolicLink(
        Paths.get(link)
            .also { linkPath -> if (Files.isSymbolicLink(linkPath)) Files.delete(linkPath) }
            .toAbsolutePath().normalize(),

        Paths.get(target)
            .toAbsolutePath().normalize()
    )
}

fun createSymbolicLinkToFile(link: Path, target: Path) {
    Files.createSymbolicLink(link, target.fileName)
}

fun downloadFile(sourceUrl: String, destination: String) {
    Fuel.download(sourceUrl)
        .fileDestination { _, _ -> File(destination) }
        .responseString()
}

fun downloadFile(sourceUrl: String, destinationPath: Path) {
    Fuel.download(sourceUrl)
        .fileDestination { _, _ -> destinationPath.toFile() }
        .responseString()
}

fun createDirectoryIfNotExist(path: Path) {
    if (Files.notExists(path)) Files.createDirectory(path)
}

fun File.hasAllFiles(fileList: List<String>): Boolean {
    val directoryFiles = list() ?: emptyArray()
    return fileList.all { it in directoryFiles }
}

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
