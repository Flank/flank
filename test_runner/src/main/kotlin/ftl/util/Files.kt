package ftl.util

import ftl.log.OutputLogLevel
import ftl.log.logLn
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.BufferedSink
import okio.buffer
import okio.sink
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

fun createSymbolicLink(link: Path, target: Path) {
    Files.createSymbolicLink(link, target.fileName)
}

fun download(sourceUrl: String, destination: Path) {
    logLn("Downloading from $sourceUrl to $destination", OutputLogLevel.DETAILED)
    val request = Request.Builder()
        .url(sourceUrl)
        .get()
        .build()
    OkHttpClient().newCall(request).execute().body?.let { responseBody ->
        val sink: BufferedSink = destination.toFile().sink().buffer()
        sink.writeAll(responseBody.source())
        sink.close()
        responseBody.close()
    }
}

fun createDirectoryIfNotExist(path: Path) {
    if (Files.notExists(path)) Files.createDirectory(path)
}

fun File.hasAllFiles(fileList: List<String>): Boolean {
    val directoryFiles = list() ?: emptyArray()
    return fileList.all { it in directoryFiles }
}
