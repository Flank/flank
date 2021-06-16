package flank.log

import org.junit.Assert.assertEquals
import org.junit.Test

private const val STATIC = "static"
private const val VALUE = "value"
private const val CLASS = "class"
private const val STRING = "string"

class LoggingTest {

    private object Singleton : Event.Type<Unit>
    private object Value : Event.Type<String>
    private data class Class(val value: String) : Event.Data

    @Test
    fun test() {
        // Given
        val formatter = buildFormatter {
            Singleton { STATIC }
            Value { this }
            Class::class { value }
            match { it as? String } to { this }
        }

        val expected = listOf(STATIC, VALUE, CLASS, STRING)
        val actual = mutableListOf<String>()
        val out = formatter.output { actual += it }.wrapEvents()

        // When
        Singleton.out()
        Value(VALUE).out()
        Class(CLASS).out()
        STRING.out()

        // Then
        assertEquals(expected, actual)
    }
}
