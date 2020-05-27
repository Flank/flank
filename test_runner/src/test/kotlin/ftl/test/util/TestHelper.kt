package ftl.test.util

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
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

inline fun <reified T : Any> ignore(): T = mockk(relaxed = true) {
    val slot = slot<Any>()
    every { this@mockk == capture(slot) } answers {
        println("match ignored: ${slot.captured}")
        true
    }
}

inline fun <reified T : Any> should(crossinline match: T.() -> Boolean): T = mockk(relaxed = true) {
    val slot = slot<T>()
    every { this@mockk == capture(slot) } answers {
        val value = slot.captured
        value.match().also { matches ->
            if (matches)
                println("match success: $value") else
                println("match failed: $value")
        }
    }
}
