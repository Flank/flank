package ftl.util

import java.io.File
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

fun unzipFile(zipFileName: File, unzipPath: String): List<File> {
    println("Unzipping: ${zipFileName.absolutePath} to $unzipPath")
    return ZipFile(zipFileName).unzipTo(unzipPath)
}

private fun ZipFile.unzipTo(unzipPath: String) = use { zipFile ->
    zipFile.entries()
        .asSequence()
        .fold(listOf<File>()) { unzippedFiles, zipFileEntry ->
            unzippedFiles + zipFileEntry.saveToFile(zipFile, unzipPath)
        }
}

private fun ZipEntry.saveToFile(zipFile: ZipFile, unzipPath: String): File {
    val outputFile = File(unzipPath, name)
    zipFile.getInputStream(this).use { zipEntryInput -> zipEntryInput.toFile(outputFile) }
    return outputFile
}

private fun InputStream.toFile(destinationFile: File) {
    destinationFile.outputStream().use { output -> copyTo(output) }
}
