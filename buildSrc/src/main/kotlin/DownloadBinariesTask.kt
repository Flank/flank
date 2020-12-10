import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import utils.createDirectoryIfNotExist
import utils.createSymbolicLink
import utils.download
import utils.hasAllFiles
import utils.isWindows
import utils.unzipFile
import java.nio.file.Files
import java.nio.file.Paths

open class DownloadBinariesTask : DefaultTask() {
    private val projectRootDir = project.rootDir.toString()
    private val outputDirectory = Paths.get(projectRootDir, "binaries")
    private val resourceDirectory = Paths.get(projectRootDir, "test_runner", "src", "main", "resources", "binaries")

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
        return !(binariesPath.exists() && binariesPath.isDirectory && binariesPath.hasAllFiles(neededFilesListByOs()))
    }

    private fun neededFilesListByOs(): List<String> = when {
        isWindows -> listOf("libatomic.so.1", "libatomic.so.1.2.0") // more files should be added after #1134"
        else -> listOf("nm", "swift-demangle", "libatomic.so.1", "libatomic.so.1.2.0")
    }

    private fun downloadAndUnzip(osname: String) {
        if(forceUpdate) resourceDirectory.toFile().deleteRecursively()
        createDirectoryIfNotExist(outputDirectory)
        val destinationFile = Paths.get(outputDirectory.toString(), "binaries.zip")

        download(
            sourceUrl = "https://github.com/Flank/binaries/releases/download/$osname/binaries.zip",
            destination = destinationFile
        )
        createDirectoryIfNotExist(resourceDirectory)
        unzipFile(destinationFile.toFile().absoluteFile, resourceDirectory.toString())
        Files.delete(destinationFile)
        createSymbolicLink(
            link = Paths.get(resourceDirectory.toString(), "libatomic.so.1"),
            target = Paths.get(resourceDirectory.toString(), "libatomic.so.1.2.0")
        )
    }
}
