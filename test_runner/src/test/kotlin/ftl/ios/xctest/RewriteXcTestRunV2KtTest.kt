package ftl.ios.xctest

import com.google.common.truth.Truth.assertThat
import ftl.config.FtlConstants
import ftl.ios.xctest.common.ONLY_TEST_IDENTIFIERS
import ftl.ios.xctest.common.getOnlyTestIdentifiers
import ftl.ios.xctest.common.getTestConfigurations
import ftl.ios.xctest.common.getTestTargets
import ftl.ios.xctest.common.parseToNSDictionary
import org.junit.Assume.assumeFalse
import org.junit.Test

class RewriteXcTestRunV2KtTest {

    @Test
    fun rewrite() {
        assumeFalse(FtlConstants.isWindows)
        // given
        val uiTestsMethods = listOf("UITests/test1")
        val secondUiTestsMethods = emptyList<String>()
        val expectedTargetNames = listOf("en", "pl")

        // when
        val result = rewriteXcTestRunV2(
            swiftXcTestRunV2,
            uiTestsMethods
        ).mapValues { (_, bytes) ->
            parseToNSDictionary(bytes)
        }

        // then
        assertThat(result.keys.toList().sorted()).isEqualTo(expectedTargetNames)
        expectedTargetNames.forEach { targetName ->
            result.getValue(targetName).getTestConfigurations().values.let { configurations ->
                assertThat(configurations.size).isEqualTo(1)
                configurations.first().getTestTargets().let { targets ->
                    assertThat(targets.size).isEqualTo(2)
                    assertThat(targets[0].getOnlyTestIdentifiers()).isEqualTo(uiTestsMethods)
                    assertThat(targets[1].getOnlyTestIdentifiers()).isEqualTo(secondUiTestsMethods)
                }
            }
        }
    }

    @Test
    fun `ensure rewrite not mutates root`() {
        assumeFalse(FtlConstants.isWindows)

        // given
        val root = parseToNSDictionary(swiftXcTestRunV2)
        val methods = findXcTestNamesV2(swiftXcTestRunV2)
        assertThat(root.toASCIIPropertyList().contains(ONLY_TEST_IDENTIFIERS)).isFalse()

        // when
        rewriteXcTestRunV2(root, methods)

        // then
        assertThat(root.toASCIIPropertyList().contains(ONLY_TEST_IDENTIFIERS)).isFalse()
    }
}
