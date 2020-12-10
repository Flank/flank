package utils

import java.io.File
import java.util.zip.ZipFile

fun unzipFile(zipFileName: File, unzipPath: String) {
    println("Unzipping: ${zipFileName.absolutePath} to $unzipPath")
    ZipFile(zipFileName).use { zipFile ->
        zipFile.entries().asSequence().forEach { zipEntry ->
            val outputFile = File(unzipPath, zipEntry.name)

            zipFile.getInputStream(zipEntry).use { zipEntryInput ->
                outputFile.outputStream().use { output ->
                    zipEntryInput.copyTo(output)
                }
            }
        }
    }
}
