package ftl.ios.xctest

import flank.common.isWindows
import ftl.args.IosArgs
import ftl.args.normalizeFilePath
import ftl.args.validate
import ftl.presentation.cli.firebase.test.ios.IosRunCommand
import ftl.ios.xctest.common.XctestrunMethods
import ftl.run.common.fromJson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assume
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.StringReader
import java.nio.file.Paths

class XcTestDataTest {

    private val testPlansPathV1 = "./src/test/kotlin/ftl/fixtures/tmp/ios/EarlGreyExample/EarlGreyExample.zip"
    private val testPlansPathV2 =
        "./src/test/kotlin/ftl/fixtures/tmp/ios/FlankTestPlansExample/FlankTestPlansExample.zip"
    private val testPlansXctestrunV1 =
        "./src/test/kotlin/ftl/fixtures/tmp/ios/EarlGreyExample/EarlGreyExampleSwiftTests.xctestrun"
    private val testPlansXctestrunV2 = "./src/test/kotlin/ftl/fixtures/tmp/ios/FlankTestPlansExample/AllTests.xctestrun"
    private val customShardingV1 = "./src/test/kotlin/ftl/fixtures/test_app_cases/custom_sharding_ios_v1.json"
    private val customShardingV2 = "./src/test/kotlin/ftl/fixtures/test_app_cases/custom_sharding_ios_v2.json"

    @get:Rule
    val root = TemporaryFolder()

    @Test
    fun testSkipTestConfiguration() {
        Assume.assumeFalse(isWindows)

        val yaml = """
        gcloud:
          test: $testPlansPathV2
          xctestrun-file: $testPlansXctestrunV2
        flank:
          skip-test-configuration: pl
        """.trimIndent()
        val xcTestRunData = IosArgs.load(yaml).validate().xcTestRunData
        assert(xcTestRunData.shardTargets.entries.map { it.key }.contains("pl").not())
        assert(xcTestRunData.shardTargets.entries.map { it.key }.contains("en"))
    }

    @Test
    fun testOnlyTestConfiguration() {
        Assume.assumeFalse(isWindows)

        val yaml = """
        gcloud:
          test: $testPlansPathV2
          xctestrun-file: $testPlansXctestrunV2
        flank:
          only-test-configuration: pl
        """.trimIndent()
        val xcTestRunData = IosArgs.load(yaml).validate().xcTestRunData
        assert(xcTestRunData.shardTargets.entries.map { it.key }.contains("pl"))
        assert(xcTestRunData.shardTargets.entries.map { it.key }.contains("en").not())
    }

    @Test
    fun `should create XcTestRunData from provided JSON -- custom sharding v1`() {
        val customSharding = fromJson<List<XctestrunMethods>>(Paths.get(customShardingV1).toFile().readText())

        val yaml = """
        gcloud:
          test: ${testPlansPathV1.normalizeFilePath()}
          xctestrun-file: ${testPlansXctestrunV1.normalizeFilePath()}
        flank:
          max-test-shards: 2
          custom-sharding-json: ${customShardingV1.normalizeFilePath()}
        """.trimIndent()

        val data = IosArgs.load(yaml).calculateXcTestRunData()

        assertEquals(1, data.shardChunks.size)
        data.shardChunks.values.forEach { chunks ->
            assertTrue(customSharding.flatMap { it.values }.containsAll(chunks.map { it.testMethodNames }))
        }

        assertEquals(1, data.shardTargets.size)
        data.shardTargets.values.forEach { xcMethodsList ->
            xcMethodsList.forEach { methods ->
                assertTrue(customSharding.flatMap { it.values }.containsAll(methods.values))
            }
        }
    }

    @Test
    fun `should create XcTestRunData from provided JSON -- custom sharding v2`() {
        val customSharding =
            fromJson<Map<String, List<XctestrunMethods>>>(Paths.get(customShardingV2).toFile().readText())

        val yaml = """
        gcloud:
          test: ${testPlansPathV2.normalizeFilePath()}
          xctestrun-file: ${testPlansXctestrunV2.normalizeFilePath()}
        flank:
          max-test-shards: 2
          custom-sharding-json: ${customShardingV2.normalizeFilePath()}
        """.trimIndent()

        val data = IosArgs.load(yaml).calculateXcTestRunData()

        assertEquals(2, data.shardChunks.size)
        assertTrue(customSharding.keys.containsAll(data.shardChunks.keys))
        data.shardChunks.entries.forEach { (plan, shards) ->
            assertEquals(if (plan == "pl") 2 else 3, shards.size)
            assertTrue(
                customSharding[plan]
                    ?.map { it.values.flatten() }
                    ?.containsAll(shards.map { it.testMethodNames })
                    ?: false
            )
        }

        assertTrue(customSharding.keys.containsAll(data.shardTargets.keys))
        assertEquals(2, data.shardTargets.size)
        data.shardTargets.entries.forEach { (plan, shards) ->
            assertEquals(if (plan == "pl") 2 else 3, shards.size)
            shards.forEach { methods ->
                assertTrue(
                    customSharding[plan]
                        ?.flatMap { it.values }
                        ?.containsAll(methods.values)
                        ?: false
                )
            }
        }
    }
}

private fun IosArgs.Companion.load(yamlData: String, cli: IosRunCommand? = null): IosArgs =
    load(StringReader(yamlData), cli)
