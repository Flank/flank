package ftl.ios.xctest

import com.dd.plist.NSArray
import com.dd.plist.NSDictionary
import com.google.common.truth.Truth.assertThat
import ftl.args.IosArgs
import ftl.config.FtlConstants
import ftl.ios.xctest.common.parseToNSDictionary
import ftl.ios.xctest.common.toByteArray
import ftl.test.util.TestHelper.normalizeLineEnding
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assume.assumeFalse
import org.junit.Test

class ReduceXcTestRunV1KtTest {

    @Test
    fun rewrite() {
        assumeFalse(FtlConstants.isWindows)

        val results = runBlocking {
            IosArgs.default().copy(
                xctestrunFile = swiftXcTestRunV1,
                testTargets = emptyList()
            ).xcTestRunFlow()
                .toList()
                .first()
                .let { String(it) }
        }

        assertThat(results.contains("OnlyTestIdentifiers")).isTrue()
    }

    @Test
    fun rewriteImmutable() {
        assumeFalse(FtlConstants.isWindows)

        val args = IosArgs.default().copy(
            xctestrunFile = swiftXcTestRunV1,
            testTargets = emptyList()
        )

        val expected = args.xcTestRunData.nsDict.toASCIIPropertyList()

        runBlocking { args.xcTestRunFlow().toList() }

        val actual = args.xcTestRunData.nsDict.toASCIIPropertyList()

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `rewrite preserves skip`() {
        val inputXml = """
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
	<key>EarlGreyExampleSwiftTests</key>
	<dict>
		<key>SkipTestIdentifiers</key>
		<array>
			<string>testTwo</string>
		</array>
		<key>OnlyTestIdentifiers</key>
		<array>
			<string>testOne</string>
			<string>testTwo</string>
		</array>
	</dict>
</dict>
</plist>
        """.trimIndent()
        val root = parseToNSDictionary(inputXml.toByteArray())

        val tests =
            mapOf("EarlGreyExampleSwiftTests" to listOf("testOne", "testTwo"))
        val rewrittenXml = root.reduceXcTestRunV1(tests)
            .toByteArray().let(::String)

        assertThat(inputXml).isEqualTo(rewrittenXml.normalizeLineEnding())
    }

    @Test
    fun `rewrite methods in single test target`() {
        assumeFalse(FtlConstants.isWindows)
        val methods = listOf(
            "FlankExampleTests/test1",
            "FlankExampleTests/test2"
        )
        val result = runBlocking {
            IosArgs.default().copy(
                xctestrunFile = multiTargetsSwiftXcTestRunV1,
                testTargets = methods
            ).xcTestRunFlow().toList().first().also {
                println(String(it))
            }
        }
        val resultXML = parseToNSDictionary(result)
        val testTarget = resultXML["FlankExampleTests"] as NSDictionary
        val resultMethods = testTarget["OnlyTestIdentifiers"] as NSArray

        assertThat(
            resultMethods.array.map { it.toJavaObject() }.toSet()
        ).isEqualTo(
            methods.toSet()
        )

        assertThat(
            resultXML["FlankExampleSecondTests"]
        ).isNull()
    }

    @Test
    fun `rewrite methods in multiple test targets`() {
        assumeFalse(FtlConstants.isWindows) // TODO enable it on #1180

        val expectedMethods1 =
            listOf("FlankExampleTests/test1", "FlankExampleTests/test2")
        val expectedMethods2 = listOf("FlankExampleSecondTests/test3")

        val result = runBlocking {
            IosArgs.default().copy(
                xctestrunFile = multiTargetsSwiftXcTestRunV1,
                testTargets = listOf(
                    expectedMethods1,
                    expectedMethods2
                ).flatten()
            ).xcTestRunFlow().toList().first()
        }
        val resultXML = parseToNSDictionary(result)

        val target1 = resultXML["FlankExampleTests"] as NSDictionary
        val target2 = resultXML["FlankExampleSecondTests"] as NSDictionary
        val resultMethods1 = target1["OnlyTestIdentifiers"] as NSArray
        val resultMethods2 = target2["OnlyTestIdentifiers"] as NSArray

        assertThat(expectedMethods1.toSet()).isEqualTo(
            resultMethods1.array.map { it.toJavaObject() }
                .toSet()
        )
        assertThat(expectedMethods2.toSet()).isEqualTo(
            resultMethods2.array.map { it.toJavaObject() }
                .toSet()
        )
    }

    @Test
    fun `rewrite incorrect methods in multiple test targets`() {
        assumeFalse(FtlConstants.isWindows) // TODO enable it on #1180

        val methods = listOf(
            "incorrect1",
            "incorrect2",
            "incorrect3"
        )

        val result = runBlocking {
            IosArgs.default().copy(
                xctestrunFile = multiTargetsSwiftXcTestRunV1,
                testTargets = methods
            ).xcTestRunFlow().toList()
        }

        assertThat(result).isEmpty()
    }

    @Test
    fun `rewrite mix of correct and incorrect methods in multiple test targets`() {
        assumeFalse(FtlConstants.isWindows) // TODO enable it on #1180

        val methods1 = listOf(
            "FlankExampleTests/test1",
            "FlankExampleTests/test2",
            "incorrect1"
        )
        val methods2 = listOf("FlankExampleSecondTests/test3", "incorrect2")

        val expectedMethods1 =
            listOf("FlankExampleTests/test1", "FlankExampleTests/test2")
        val expectedMethods2 = listOf("FlankExampleSecondTests/test3")

        val result = runBlocking {
            IosArgs.default().copy(
                xctestrunFile = multiTargetsSwiftXcTestRunV1,
                testTargets = listOf(methods1, methods2).flatten()
            ).xcTestRunFlow().toList().first()
        }
        val resultXML = parseToNSDictionary(result)

        val target1 = resultXML["FlankExampleTests"] as NSDictionary
        val target2 = resultXML["FlankExampleSecondTests"] as NSDictionary
        val resultMethods1 = target1["OnlyTestIdentifiers"] as NSArray
        val resultMethods2 = target2["OnlyTestIdentifiers"] as NSArray

        assertThat(expectedMethods1.toSet()).isEqualTo(
            resultMethods1.array.map { it.toJavaObject() }
                .toSet()
        )
        assertThat(expectedMethods2.toSet()).isEqualTo(
            resultMethods2.array.map { it.toJavaObject() }
                .toSet()
        )
    }
}
