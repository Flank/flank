@file:Repository("https://repo1.maven.org/maven2/org/rauschig/jarchivelib/")
@file:DependsOn("org.rauschig:jarchivelib:1.1.0")
@file:DependsOn("org.tukaani:xz:1.0")

import org.rauschig.jarchivelib.*
import java.io.File

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

