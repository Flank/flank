@file:Repository("https://repo1.maven.org/maven2/org/rauschig/jarchivelib/")
@file:DependsOn("org.rauschig:jarchivelib:1.1.0")
@file:DependsOn("org.tukaani:xz:1.0")

import org.rauschig.jarchivelib.*
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
    kotlin.runCatching {
        archiver.extract(this, destination)
    }.onFailure {
        println("There was an error when unpacking $name")
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
    kotlin.runCatching {
        archiver.extract(this, destination)
    }.onFailure {
        println("There was an error when unpacking $name")
    }
}
