package ftl.ios.xctest

import flank.common.isWindows
import ftl.args.IosArgs
import ftl.args.validate
import ftl.presentation.cli.firebase.test.ios.IosRunCommand
import org.junit.Assume
import org.junit.Test
import java.io.StringReader

class XcTestDataTest {

    private val testPlansPath = "./src/test/kotlin/ftl/fixtures/tmp/ios/FlankTestPlansExample/FlankTestPlansExample.zip"
    private val testPlansXctestrun = "./src/test/kotlin/ftl/fixtures/tmp/ios/FlankTestPlansExample/AllTests.xctestrun"

    @Test
    fun testSkipTestConfiguration() {
        Assume.assumeFalse(isWindows)

        val yaml = """
        gcloud:
          test: $testPlansPath
          xctestrun-file: $testPlansXctestrun
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
          test: $testPlansPath
          xctestrun-file: $testPlansXctestrun
        flank:
          only-test-configuration: pl
        """.trimIndent()
        val xcTestRunData = IosArgs.load(yaml).validate().xcTestRunData
        assert(xcTestRunData.shardTargets.entries.map { it.key }.contains("pl"))
        assert(xcTestRunData.shardTargets.entries.map { it.key }.contains("en").not())
    }
}

private fun IosArgs.Companion.load(yamlData: String, cli: IosRunCommand? = null): IosArgs =
    load(StringReader(yamlData), cli)
