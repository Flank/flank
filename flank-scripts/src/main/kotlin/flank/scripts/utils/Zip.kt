package flank.scripts.utils

import java.io.File
import java.util.zip.ZipFile


fun unzip(src: String, dst: String) {
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
