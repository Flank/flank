package flank.corellium.cli

import flank.corellium.api.Authorization
import flank.corellium.domain.RunTestCorelliumAndroid.Args
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import picocli.CommandLine
import java.io.File

class RunTestCorelliumAndroidCommandTest {

    /**
     * Apply test values to config. Each value should be different than default.
     */
    private fun RunTestCorelliumAndroidCommand.Config.applyTestValues() = apply {
        auth = "test_auth.yml"
        project = "test project"
        apks = listOf(
            Args.Apk.App(
                "app1.apk",
                tests = listOf(
                    Args.Apk.Test("app1-test1.apk"),
                )
            ),
            Args.Apk.App(
                "app2.apk",
                tests = listOf(
                    Args.Apk.Test("app2-test1.apk"),
                    Args.Apk.Test("app2-test2.apk"),
                )
            ),
        )
        maxTestShards = Int.MAX_VALUE
        localResultsDir = "test_result_dir"
        obfuscate = true
    }

    /**
     * The test is checking if the configuration is parsing and loading correctly.
     * To ensure that, the expected config is checked against default, parsed and loaded.
     */
    @Test
    fun configTest() {
        // ======================== GIVEN ========================

        // Create expected config for assertions
        // Make sure to set values different then produced by defaultConfig() function,
        // otherwise the test will fail
        val expectedConfig = RunTestCorelliumAndroidCommand.Config().applyTestValues()

        val cliArgs = expectedConfig.run {
            arrayOf(
                "--project=$project",
                "--auth=$auth",
                "--apks=app1.apk=app1-test1.apk;app2.apk=app2-test1.apk,app2-test2.apk",
                "--max-test-shards=$maxTestShards",
                "--local-result-dir=$localResultsDir",
                "--obfuscate=$obfuscate",
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
max-test-shards: $maxTestShards
local-result-dir: $localResultsDir
obfuscate: $obfuscate
            """.trimIndent()
        }

        // ======================== WHEN ========================

        // Obtain default config for assertions
        val defaultConfig = RunTestCorelliumAndroidCommand().config

        // Parse config from the CLI arguments.
        val parsedConfig = RunTestCorelliumAndroidCommand()
            .also { command -> CommandLine(command).parseArgs(*cliArgs) }
            .config

        // Load config from file.
        val loadedConfig = RunTestCorelliumAndroidCommand().apply {
            // Manually set path to yaml config file
            yamlConfigPath = File("test_config_${System.nanoTime()}.yml").apply {
                writeText(yamlConfig) // Create YAML config file for test
                deleteOnExit()
            }.absolutePath
        }.config

        // ======================== THEN ========================

        // Make sure each config has initialized the same set of properties.
        assertEquals(expectedConfig.data.keys, defaultConfig.data.keys)
        assertEquals(expectedConfig.data.keys, parsedConfig.data.keys)
        assertEquals(expectedConfig.data.keys, loadedConfig.data.keys)

        // For each expected value, check is:
        expectedConfig.data.forEach { (key, expected) ->
            // equal to parsed.
            assertEquals(expected, parsedConfig.data[key])
            // equal to loaded.
            assertEquals(expected, loadedConfig.data[key])
            // different than default.
            assertNotEquals(expected, defaultConfig.data[key])
        }
    }

    /**
     * The test is checking if args [RunTestCorelliumAndroidCommand.args] are generated correctly, basing on the given [RunTestCorelliumAndroidCommand.Config]
     */
    @Test
    fun argsTest() {
        // ======================== GIVEN ========================

        val testConfig = RunTestCorelliumAndroidCommand.Config().applyTestValues()

        val expectedCredentials = Authorization.Credentials(
            host = "api.host.io",
            username = "user",
            password = "pass",
        )

        val expected = testConfig.run {
            Args(
                credentials = expectedCredentials,
                apks = apks!!,
                maxShardsCount = maxTestShards!!,
                outputDir = localResultsDir!!,
                obfuscateDumpShards = obfuscate!!
            )
        }

        val yamlAuth = expected.credentials.run {
            """
host: $host
username: $username
password: $password
            """.trimIndent()
        }

        // Prepare yaml credentials file
        File(testConfig.auth!!).apply {
            writeText(yamlAuth)
            deleteOnExit()
        }

        // ======================== WHEN ========================

        val actual = RunTestCorelliumAndroidCommand()
            .apply { config.data += testConfig.data } // setup command config
            .args // calculate & get args

        // ======================== THEN ========================

        assertEquals(expected, actual)
    }
}
