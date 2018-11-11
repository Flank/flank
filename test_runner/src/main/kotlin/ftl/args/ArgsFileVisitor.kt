package ftl.args

import ftl.util.Utils.fatalError
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
    val result: MutableList<Path> = mutableListOf()

    @Throws(IOException::class)
    override fun visitFile(path: Path, attrs: BasicFileAttributes): FileVisitResult {
        if (pathMatcher.matches(path)) {
            result.add(path)
        }
        return FileVisitResult.CONTINUE
    }

    override fun visitFileFailed(file: Path?, exc: IOException?): FileVisitResult {
        fatalError("Failed to visit $file $exc")
        return FileVisitResult.CONTINUE
    }

    private val RECURSE = "/**"

    @Throws(java.nio.file.NoSuchFileException::class)
    fun walk(searchPath: Path): List<Path> {
        val searchString = searchPath.toString()
        // /Users/tmp/code/flank/test_app/** => /Users/tmp/code/flank/test_app/
        val beforeGlob = Paths.get(searchString.substringBefore(RECURSE))
        // must not follow links when resolving paths or /tmp turns into /private/tmp
        val realPath = beforeGlob.toRealPath(LinkOption.NOFOLLOW_LINKS)

        val searchDepth = if (searchString.contains(RECURSE)) {
            Integer.MAX_VALUE
        } else {
            1
        }

        Files.walkFileTree(realPath, EnumSet.of(FileVisitOption.FOLLOW_LINKS), searchDepth, this)
        return this.result
    }
}
