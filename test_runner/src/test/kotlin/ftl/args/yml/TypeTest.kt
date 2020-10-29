package ftl.args.yml

import com.google.common.truth.Truth.assertThat
import ftl.run.exception.FlankGeneralError
import org.junit.Test

internal class TypeTest {

    @Test(expected = FlankGeneralError::class)
    fun `Should throw exception when unknow type parse`() {
        "unknown test type".toType()
    }

    @Test
    fun `Should properly parse know type`() {
        // given
        val correctTypes = mapOf(
            "instrumentation" to Type.INSTRUMENTATION,
            "robo" to Type.ROBO,
            "xctest" to Type.XCTEST,
            "game-loop" to Type.GAMELOOP
        )

        // when
        val actual = correctTypes.keys.map { it.toType() }

        // then
        correctTypes.values.forEachIndexed { index, type ->
            assertThat(type).isEqualTo(actual[index])
        }
    }
}
