package flank.corellium.cli.test.android.task

import flank.corellium.cli.TestAndroidCommand
import flank.corellium.cli.TestAndroidCommand.Config
import flank.exection.parallel.invoke
import flank.exection.parallel.select
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import picocli.CommandLine
import java.io.File

class ConfigKtTest {

    /**
     * The test is checking if the configuration is parsing and loading correctly.
     * To ensure that, the expected config is checked against default, parsed and loaded.
     */
    @Test
    fun test() {
        // ======================== GIVEN ========================

        // Create expected config for assertions
        // Make sure to set values different from produced by defaultConfig() function,
        // otherwise the test will fail
        val expectedConfig = Config().applyTestValues()

        val cliArgs = expectedConfig.run {
            arrayOf(
                "--project=$project",
                "--auth=$auth",
                "--apks=app1.apk=app1-test1.apk;app2.apk=app2-test1.apk,app2-test2.apk",
                "--test-targets=class foo.Foo,package bar",
                "--max-test-shards=$maxTestShards",
                "--local-result-dir=$localResultsDir",
                "--obfuscate=$obfuscate",
                "--gpu-acceleration=$gpuAcceleration",
                "--scan-previous-durations=$scanPreviousDurations",
                "--num-flaky-test-attempts=$flakyTestAttempts",
                "--junit-report-config=test1=Passed;test2=FAILED,flaky"
            )
        }

        val yamlConfig = expectedConfig.run {
            """
            auth: $auth
            project: $project
            apks:
            - path: "app1.apk"
              tests:
              - path: "app1-test1.apk"
            - path: "app2.apk"
              tests:
              - path: "app2-test1.apk"
              - path: "app2-test2.apk"
            test-targets:
              - class foo.Foo
              - package bar
            max-test-shards: $maxTestShards
            local-result-dir: $localResultsDir
            obfuscate: $obfuscate
            gpu-acceleration: $gpuAcceleration
            scan-previous-durations: $scanPreviousDurations
            num-flaky-test-attempts: $flakyTestAttempts
            junit-report-config:
              test1: [Passed]
              test2: [FAILED, flaky]
            """.trimIndent()
        }

        fun TestAndroidCommand.loadConfig() = runBlocking {
            setOf(config)(TestAndroidCommand to this@loadConfig).last().select(Config)
        }

        // ======================== WHEN ========================

        // Obtain default config for assertions
        val defaultConfig = TestAndroidCommand().loadConfig()

        // Parse config from the CLI arguments.
        val parsedConfig = TestAndroidCommand().also { command ->
            CommandLine(command).parseArgs(*cliArgs)
        }.loadConfig()

        // Load config from file.
        val loadedConfig = TestAndroidCommand().apply {
            // Manually set path to yaml config file
            yamlConfigPath = File("test_config_${System.nanoTime()}.yml").apply {
                writeText(yamlConfig) // Create YAML config file for test
                deleteOnExit()
            }.absolutePath
        }.loadConfig()

        // ======================== THEN ========================

        // Make sure each config has initialized the same set of properties.
        Assert.assertEquals(expectedConfig.data.keys, defaultConfig.data.keys)
        Assert.assertEquals(expectedConfig.data.keys, parsedConfig.data.keys)
        Assert.assertEquals(expectedConfig.data.keys, loadedConfig.data.keys)

        // For each expected value, check is:
        expectedConfig.data.forEach { (key, expected) ->
            // equal to parsed.
            Assert.assertEquals(expected, parsedConfig.data[key])
            // equal to loaded.
            Assert.assertEquals(expected, loadedConfig.data[key])
            // different from default.
            Assert.assertNotEquals(expected, defaultConfig.data[key])
        }
    }
}
