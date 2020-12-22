package flank.common

import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream


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
    if(isDirectory) {
        outputFile.mkdirs()
    } else {
        zipFile.getInputStream(this).use { zipEntryInput -> zipEntryInput.toFile(outputFile) }
    }

    return outputFile
}

private fun InputStream.toFile(destinationFile: File) {
    destinationFile.outputStream().use { output -> copyTo(output) }
}

fun zip(src: File, dst: File) {
    print("* Zipping: $src to $dst - ")
    dst.apply {
        if (isDirectory) throw Exception("Destination path $dst cannot be directory")
        if (!exists()) createNewFile()
        outputStream().use { fos ->
            ZipOutputStream(fos).use { zos ->
                zos.zipFile(
                    fileToZip = src,
                    fileName = ""
                )
            }
        }
    }
    println("OK")
}

private fun ZipOutputStream.zipFile(
    fileToZip: File,
    fileName: String
) {
    when {
        fileToZip.isHidden -> Unit
        fileToZip.isDirectory -> {
            putNextEntry(
                ZipEntry(
                    if (fileName.endsWith("/"))
                        fileName else
                        "$fileName/"
                )
            )
            closeEntry()
            fileToZip.listFiles()?.forEach { childFile ->
                zipFile(childFile, "$fileName/${childFile.name}")
            }
        }
        else -> {
            putNextEntry(ZipEntry(fileName))
            FileInputStream(fileToZip).copyTo(this)
        }
    }
}
