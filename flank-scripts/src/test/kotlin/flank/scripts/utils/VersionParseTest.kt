package flank.scripts.utils

import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class VersionParseTest(
    private val left: String,
    private val right: String,
    private val expectedResult: Compare
) {
    companion object {

        @JvmStatic
        @Parameterized.Parameters(name = "compare {0} and {1} with result {2}")
        fun inputs() = listOf(
            arrayOf("6.6.1", "6.7", Smaller),
            arrayOf("1", "2", Smaller),
            arrayOf("2.0.0", "1.9.9.9", Greater),
            arrayOf("2.0.0", "2.0.0-rc-1", Greater),
            arrayOf("2.0.0-rc-1", "2.0", Smaller),
            arrayOf("6.7.1", "6.8-rc-1", Smaller),
            arrayOf("v1beta3-rev20201029-1.30.10", "v1beta3-rev20201028-1.30.10", Greater),
            arrayOf("v1beta3-rev20201029-1.30.10", "v1beta3-rev20201228-1.30.10", Smaller),
            arrayOf("1.2.3", "1.2.3.0", Equal),
            arrayOf("1.2.3.0", "1.2.3", Equal),
            arrayOf("6.8.1-rc-1", "6.8.1-rc-2", Smaller),
        )
    }

    @Test
    fun `compare versions`() {
        // We do not use > operator to test equality
        val result = parseToVersion(left).compareTo(parseToVersion(right))
        assertTrue(
            "$left should be evaluated as ${expectedResult.name()} $right: result = $result",
            expectedResult(result)
        )
    }
}

sealed class Compare(private val lambda: (Int) -> Boolean) {
    operator fun invoke(int: Int) = lambda(int)
    fun name() = this::class.simpleName?.uppercase() ?: ""
    override fun toString() = name()
}

private object Greater : Compare({ it > 0 })
private object Smaller : Compare({ it < 0 })
private object Equal : Compare({ it == 0 })
