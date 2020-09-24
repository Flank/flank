package flank.scripts.utils

import java.io.File
import java.io.FileInputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

fun unzip(src: String, dst: String = File(src).parent) {
    println("Unzipping: $src to $dst")
    ZipFile(src).use { zipFile ->
        zipFile.entries().asSequence().forEach { zipEntry ->
            val outputFile = File(dst, zipEntry.name)

            if (zipEntry.isDirectory) {
                outputFile.mkdirs()
                return@forEach
            }

            outputFile.parentFile.mkdirs()

            zipFile.getInputStream(zipEntry).use { zipEntryInput ->
                outputFile.outputStream().use { output ->
                    zipEntryInput.copyTo(output)
                }
            }
        }
    }
}

fun zip(src: String, dst: String) {
    println("Zipping: $src to $dst")
    File(dst).apply {
        if (isDirectory) throw Exception("Destination path $dst cannot be directory")
        if (!exists()) createNewFile()
        outputStream().use { fos ->
            ZipOutputStream(fos).use { zos ->
                zos.zipFile(
                    fileToZip = File(src),
                    fileName = ""
                )
            }
        }
    }
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
            fileToZip.listFiles()!!.forEach { childFile ->
                zipFile(childFile, "$fileName/${childFile.name}")
            }
        }
        else -> {
            putNextEntry(ZipEntry(fileName))
            FileInputStream(fileToZip).copyTo(this)
        }
    }
}
