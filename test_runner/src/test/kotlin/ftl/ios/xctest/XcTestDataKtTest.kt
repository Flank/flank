package ftl.ios.xctest

import ftl.args.IosArgs
import ftl.run.common.prettyPrint
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class XcTestDataKtTest {

    @get:Rule
    val root = TemporaryFolder()

    @Test
    fun `should create XcTestRunData from provided JSON -- custom sharding v1`() {
        val customSharding = listOf(
            listOf(
                "EarlGreyExampleSwiftTests/testThatThrows",
                "EarlGreyExampleSwiftTests/testWithCustomMatcher",
                "EarlGreyExampleSwiftTests/testBasicSelectionAndAction"
            ),
            listOf(
                "EarlGreyExampleSwiftTests/testLayout",
                "EarlGreyExampleSwiftTests/testCollectionMatchers",
                "EarlGreyExampleSwiftTests/testTableCellOutOfScreen"
            ),
            listOf(
                "EarlGreyExampleSwiftTests/testWithGreyAssertions",
                "EarlGreyExampleSwiftTests/testCatchErrorOnFailure",
                "EarlGreyExampleSwiftTests/testWithCustomAssertion"
            ),
            listOf(
                "EarlGreyExampleSwiftTests/testWithInRoot",
                "EarlGreyExampleSwiftTests/testBasicSelection",
                "EarlGreyExampleSwiftTests/testBasicSelectionAndAssert",
                "EarlGreyExampleSwiftTests/testSelectionOnMultipleElements"
            ),
            listOf(
                "EarlGreyExampleSwiftTests/testCustomAction",
                "EarlGreyExampleSwiftTests/testWithCondition",
                "EarlGreyExampleSwiftTests/testWithCustomFailureHandler",
                "EarlGreyExampleSwiftTests/testBasicSelectionActionAssert"
            )
        )

        val customShardingFile = root.newFile("custom_sharding.json").also {
            it.writeText(prettyPrint.toJson(customSharding))
        }

        val data = IosArgs.default().run {
            copy(
                xctestrunZip = "./src/test/kotlin/ftl/fixtures/tmp/ios/EarlGreyExample/EarlGreyExample.zip",
                xctestrunFile = "./src/test/kotlin/ftl/fixtures/tmp/ios/EarlGreyExample/EarlGreyExampleSwiftTests.xctestrun",
                commonArgs = commonArgs.copy(
                    maxTestShards = 2,
                    shardingJson = customShardingFile.absolutePath
                )
            )
        }.calculateXcTestRunData()

        data.shardChunks.values.forEach { chunks ->
            assertTrue(customSharding.containsAll(chunks.map { it.testMethodNames }))
        }

        data.shardTargets.values.forEach {
            it.forEach { methods ->
                assertTrue(customSharding.containsAll(methods.values))
            }
        }
    }

    @Test
    fun `should create XcTestRunData from provided JSON -- custom sharding v2`() {
        val customSharding = mapOf(
            "en" to listOf(
                listOf(
                    "SecondUITestsClass/test2_PLLocale",
                    "SecondUITestsClass/test2_3",
                    "SecondUITestsClass/test2_ENLocale"
                ),
                listOf(
                    "UITestsClass/test1_1"
                ),
                listOf(
                    "UITestsClass/test1_ENLocale",
                    "UITestsClass/test1_PLLocale",
                    "UITestsClass/test1_2",
                    "UITestsClass/test1_3"
                ),
                listOf(
                    "SecondUITestsClass/test2_1",
                    "SecondUITestsClass/test2_2"
                )
            ),
            "pl" to listOf(
                listOf(
                    "SecondUITestsClass/test2_PLLocale",
                    "SecondUITestsClass/test2_3",
                    "SecondUITestsClass/test2_ENLocale"
                ),
                listOf(
                    "UITestsClass/test1_1",
                    "UITestsClass/test1_ENLocale",
                    "UITestsClass/test1_PLLocale"
                ),
                listOf(
                    "SecondUITestsClass/test2_1",
                    "SecondUITestsClass/test2_2"
                ),
                listOf(
                    "UITestsClass/test1_2",
                    "UITestsClass/test1_3"
                )
            )

        )

        val customShardingFile = root.newFile("custom_sharding.json").also {
            it.writeText(prettyPrint.toJson(customSharding))
        }

        val data = IosArgs.default().run {
            copy(
                xctestrunZip = "./src/test/kotlin/ftl/fixtures/tmp/ios/FlankTestPlansExample/FlankTestPlansExample.zip",
                xctestrunFile = "./src/test/kotlin/ftl/fixtures/tmp/ios/FlankTestPlansExample/AllTests.xctestrun",
                commonArgs = commonArgs.copy(
                    maxTestShards = 5,
                    shardingJson = customShardingFile.absolutePath
                )
            )
        }.calculateXcTestRunData()

        data.shardChunks.entries.forEach { (plan, shards) ->
            assertTrue(customSharding[plan]?.containsAll(shards.map { it.testMethodNames }) ?: false)
        }

        data.shardTargets.entries.forEach { (plan, shards) ->
            shards.forEach { methods ->
                assertTrue(customSharding[plan]?.containsAll(methods.values) ?: false)
            }
        }
    }
}
