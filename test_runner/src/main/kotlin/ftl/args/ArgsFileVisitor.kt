package ftl.args

import ftl.run.exception.FlankGeneralError
import java.io.IOException
import java.nio.file.FileSystems
import java.nio.file.FileVisitOption
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import java.util.EnumSet

class ArgsFileVisitor(glob: String) : SimpleFileVisitor<Path>() {
    private val pathMatcher = FileSystems.getDefault().getPathMatcher(glob)
    private val result: MutableList<Path> = mutableListOf()

    @Throws(IOException::class)
    override fun visitFile(path: Path, attrs: BasicFileAttributes): FileVisitResult {
        if (pathMatcher.matches(path)) {
            result.add(path)
        }
        return FileVisitResult.CONTINUE
    }

    override fun visitFileFailed(file: Path?, exc: IOException?): FileVisitResult {
        // java.nio.file.AccessDeniedException: /tmp/systemd-private-2bc4cd4c824142ab95fb18cbb14165f5-systemd-timesyncd.service-epYUoK
        System.err.println("Failed to visit $file ${exc?.message}")
        return FileVisitResult.CONTINUE
    }

    companion object {
        private const val RECURSE = "/**"
        private const val SINGLE_GLOB = "/*"
    }

    @Throws(java.nio.file.NoSuchFileException::class)
    fun walk(searchPath: Path): List<Path> {
        val searchString = searchPath.toString()
        // /Users/tmp/code/flank/test_projects/android/** => /Users/tmp/code/flank/test_projects/android/
        // /Users/tmp/code/*                 => /Users/tmp/code/
        val beforeGlob = Paths.get(searchString.substringBefore(SINGLE_GLOB))
        // must not follow links when resolving paths or /tmp turns into /private/tmp
        val realPath = try {
            beforeGlob.toRealPath(LinkOption.NOFOLLOW_LINKS)
        } catch (e: java.nio.file.NoSuchFileException) {
            throw FlankGeneralError("Failed to resolve path $searchPath")
        }

        val searchDepth = if (searchString.contains(RECURSE)) Integer.MAX_VALUE else 1

        Files.walkFileTree(realPath, EnumSet.of(FileVisitOption.FOLLOW_LINKS), searchDepth, this)
        return this.result
    }
}
