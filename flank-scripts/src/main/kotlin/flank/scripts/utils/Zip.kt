package flank.scripts.utils

import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

fun unzip(src: File, dst: File = src.parentFile) {
    print("* Unzipping: $src to $dst - ")
    ZipFile(src).use { zipFile ->
        zipFile.entries().asSequence().forEach { zipEntry ->
            val outputFile = File(dst, zipEntry.name)

            if (zipEntry.isDirectory) {
                outputFile.mkdirs()
                return@forEach
            }

            outputFile.parentFile.mkdirs()

            zipFile.getInputStream(zipEntry) useCopyTo outputFile.outputStream()
        }
    }
    println("OK")
}

private infix fun InputStream.useCopyTo(output: OutputStream) =
    use { zipEntryInput -> output.use { output -> zipEntryInput.copyTo(output) } }

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
