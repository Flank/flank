package flank.log

import org.junit.Assert.assertEquals
import org.junit.Test

private const val STATIC = "static"
private const val VALUE = "value"
private const val CLASS = "class"
private const val STRING = "string"

/**
 * Complete logger API test.
 */
class LoggingTest {

    private object Singleton : Event.Type<Unit>
    private object Value : Event.Type<String>
    private data class Class(val value: String) : Event.Data

    /**
     * Test logging API.
     *
     * * Build [Formatter] with different types of formatting functions.
     * * Create output with events wrapper.
     * * Pass various objects to logger output.
     */
    @Test
    fun test() {
        // Given
        val expected = listOf(STATIC, VALUE, CLASS, STRING)
        val actual = mutableListOf<String>()

        // Specify formatter for converting registered types into string values
        val formatter = buildFormatter<String> {
            Singleton { STATIC }
            Value { this }
            Class::class { value }
            match { it as? String } to { this }
        }

        // Prepare log output from string formatter
        val out: Output = formatter
            .output { formatted -> actual += formatted }
            .normalize { next -> Unit event next }

        // When
        Singleton.out()
        Value(VALUE).out()
        Class(CLASS).out()
        STRING.out()

        // Then
        assertEquals(expected, actual)
    }
}
