package flank.scripts.utils

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class VersionToStringTest(
    private val left: String,
) {
    companion object {

        @JvmStatic
        @Parameterized.Parameters
        fun inputs() = listOf(
            "6.6.1",
            "1",
            "2.0.0",
            "2.0.0-rc-1",
            "2.0",
            "6.8-rc-1",
            "v1beta3-rev20201029-1.30.10",
            "1.2.3.0",
            "1.10.3-jdk8"
        ).map { arrayOf(it) }
    }

    @Test
    fun `compare strings`() {
        Assert.assertEquals(left, parseToVersion(left).toString())
    }
}
