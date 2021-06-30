package flank.filter.internal.factory

import flank.filter.internal.Test
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

internal fun fromTestFile(args: List<String>): Test.Filter {
    require(args.size == 1) { "Invalid file path" }
    val path = Paths.get(args[0])
    try {
        val lines = Files.readAllLines(path)
        // this is really an implementation detail:
        // being the package name most generic one, it is able to filter properly if you pass the package name,
        // the fully qualified class name or the fully qualified method name.
        return withPackageName(lines)
    } catch (e: IOException) {
        throw IllegalArgumentException("Unable to read testFile", e)
    }
}
