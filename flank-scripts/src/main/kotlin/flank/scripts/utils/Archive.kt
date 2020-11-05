package flank.scripts.utils

import org.rauschig.jarchivelib.ArchiveFormat
import org.rauschig.jarchivelib.ArchiverFactory
import org.rauschig.jarchivelib.CompressionType
import java.io.File

fun File.extract(
    destination: File,
    archiveFormat: ArchiveFormat = ArchiveFormat.AR,
    compressFormat: CompressionType? = null
) {
    println("Unpacking...$name")
    val archiver = if (compressFormat != null) {
        ArchiverFactory.createArchiver(archiveFormat, compressFormat)
    } else {
        ArchiverFactory.createArchiver(archiveFormat)
    }
    runCatching {
        archiver.extract(this, destination)
    }.onFailure {
        println("There was an error when unpacking $name - $it")
    }
}

fun File.extract(
    destination: File,
    archiveFormat: String,
    compressFormat: String? = null
) {
    println("Unpacking...$name")
    val archiver = if (compressFormat != null) {
        ArchiverFactory.createArchiver(archiveFormat, compressFormat)
    } else {
        ArchiverFactory.createArchiver(archiveFormat)
    }
    runCatching {
        archiver.extract(this, destination)
    }.onFailure {
        println("There was an error when unpacking $name - $it")
    }
}

fun List<File>.archive(
    destinationFileName: String,
    destinationDirectory: File,
    archiveFormat: ArchiveFormat = ArchiveFormat.ZIP
) {
    println("Packing...$destinationFileName")
    val archiver = ArchiverFactory.createArchiver(archiveFormat)
    runCatching {
        archiver.create(destinationFileName, destinationDirectory, *toTypedArray())
    }.onFailure {
        println("There was an error when packing ${destinationDirectory.absolutePath}${File.separator}$destinationFileName - $it")
    }
}
