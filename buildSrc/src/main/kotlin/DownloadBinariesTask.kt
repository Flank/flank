import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
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
    private val resourceDirectory = Paths.get(project.rootDir.toString(), "test_runner", "src", "main", "resources", "binaries")

    @Input
    var forceUpdate: Boolean = false

    @TaskAction
    fun downloadBinaries() {
        osName
            .takeIf { forceUpdate || shouldDownloadBinaries() }
            ?.let(::downloadAndUnzip)
    }

    private val osName: String
        get() = if(isWindows) "windows" else "linux"

    private fun shouldDownloadBinaries(): Boolean {
        val binariesPath = resourceDirectory.toFile()
        return !(binariesPath.exists() && binariesPath.isDirectory && binariesPath.hasAllNeededFiles())
    }

    private fun File.hasAllNeededFiles(): Boolean {
        val binaries = list() ?: emptyArray()
        return neededFilesListByOs().all { it in binaries }
    }

    private fun neededFilesListByOs(): List<String> = when {
        isWindows -> listOf("libatomic.so.1", "libatomic.so.1.2.0") // more files should be added after #1134"
        else -> listOf("nm", "swift-demangle", "libatomic.so.1", "libatomic.so.1.2.0")
    }

    private fun downloadAndUnzip(osname: String) {
        if(forceUpdate) resourceDirectory.toFile().deleteRecursively()
        if(Files.notExists(outputDirectory)) Files.createDirectory(outputDirectory)
        val sourceUrl = "https://github.com/Flank/binaries/releases/download/$osname/binaries.zip"
        val destinationFile = Paths.get(outputDirectory.toString(), "binaries.zip").toFile()
        ant.invokeMethod("get", mapOf("src" to sourceUrl, "dest" to destinationFile))
        if(Files.notExists(resourceDirectory)) Files.createDirectory(resourceDirectory)
        unzipFile(destinationFile.absoluteFile, resourceDirectory.toString())
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
