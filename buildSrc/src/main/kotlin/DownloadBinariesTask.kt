import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.zip.ZipFile

open class DownloadBinariesTask : DefaultTask() {

    private val isWindows: Boolean
        get() = DefaultNativePlatform.getCurrentOperatingSystem().isWindows

    private val outputDirectory = Paths.get(project.rootDir.toString(), "binaries")

    @TaskAction
    fun downloadBinaries() {
        osName
            ?.takeIf { shouldDownloadBinaries() }
            ?.let(::downloadAndUnzip)
    }

    private val osName: String?
        get() = if(isWindows) "windows" else "linux"

    private fun shouldDownloadBinaries(): Boolean {
        val binariesPath = outputDirectory.toFile()
        return !(binariesPath.exists() && binariesPath.isDirectory && hasAllNeededFiles())
    }

    private fun hasAllNeededFiles(): Boolean {
        val binaries = outputDirectory.toFile().list() ?: emptyArray()
        return filesListByOs().all { it in binaries }
    }

    private fun filesListByOs(): List<String> = when {
        isWindows -> listOf("libatomic.so.1", "libatomic.so.1.2.0") // more files should be added after #1134"
        else -> listOf("nm", "swift-demangle", "libatomic.so.1", "libatomic.so.1.2.0")
    }

    private fun downloadAndUnzip(osname: String) {
        if(Files.notExists(outputDirectory)) {
            Files.createDirectory(outputDirectory)
        }
        val sourceUrl = "https://github.com/Flank/binaries/releases/download/$osname/binaries.zip"
        val destinationFile = Paths.get(outputDirectory.toString(), "binaries.zip").toFile()
        ant.invokeMethod("get", mapOf("src" to sourceUrl, "dest" to destinationFile))
        unzipFile(destinationFile.absoluteFile, outputDirectory.toString())
        destinationFile.delete()
    }

    private fun unzipFile(fileName: File, unzipPath: String) {
        println("Unzipping: ${fileName.absolutePath} to $unzipPath")
        ZipFile(fileName).use { zipFile ->
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

}
