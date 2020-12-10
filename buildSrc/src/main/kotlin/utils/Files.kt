package utils

import org.gradle.api.DefaultTask
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

fun createSymbolicLink(link: Path, target: Path) {
    Files.createSymbolicLink(link, target.fileName)
}

fun DefaultTask.download(sourceUrl: String, destination: Path) {
    ant.invokeMethod("get", mapOf("src" to sourceUrl, "dest" to destination.toFile()))
}

fun createDirectoryIfNotExist(path: Path) {
    if(Files.notExists(path)) Files.createDirectory(path)
}

fun File.hasAllFiles(fileList: List<String>): Boolean {
    val directoryFiles = list() ?: emptyArray()
    return fileList.all { it in directoryFiles }
}
