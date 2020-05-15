package ftl.test.util

import org.junit.Assert
import java.nio.file.Path
import java.nio.file.Paths

object TestHelper {

    fun assert(actual: Any?, expected: Any?) =
        Assert.assertEquals(expected, actual)

    fun getPath(path: String): Path =
        Paths.get(path).toAbsolutePath().normalize()

    fun getString(path: String): String =
        getPath(path).toString()

    fun String.absolutePath(): String = Paths.get(this).toAbsolutePath().normalize().toString()

    fun String.normalizeLineEnding(): String {
        // required for tests to pass on Windows
        return this.replace("\r\n", "\n")
    }

    fun getThrowable(action: () -> Unit): Throwable = try {
        action()
        throw FlankTestNotFoundException("Action not throwing exception")
    } catch (exception: Throwable) {
        exception
    }
}
