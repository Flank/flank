@file:Suppress("RemoveCurlyBracesFromTemplate")

package ftl.test.util

import ftl.Main
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.Assert
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import picocli.CommandLine
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.reflect.KClass

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
        println("${this@mockk} match ignored: ${slot.captured}")
        true
    }
}

inline fun <reified T : Any> should(crossinline match: T.() -> Boolean): T = mockk(relaxed = true) {
    val slot = slot<T>()
    var matched = false
    every { this@mockk == capture(slot) } answers {
        val value = slot.captured
        value.match().also { matches ->
            matched = matches
            if (matches)
                println("${this@mockk} match succeed: $value") else
                println("${this@mockk} match failed: $value")
        }
    }
    every { this@mockk.toString() } answers {
        if (matched && slot.isCaptured)
            slot.captured.toString() else
            callOriginal()
    }
}

internal fun <T : Throwable> assertThrowsWithMessage(clazz: KClass<T>, message: String, block: () -> Unit) {
    assertThrows(clazz.java) { block() }.also { assertTrue(it.message?.contains(message) ?: false) }
}

internal fun runMainCommand(vararg args: String) = CommandLine(Main()).execute(*args)
