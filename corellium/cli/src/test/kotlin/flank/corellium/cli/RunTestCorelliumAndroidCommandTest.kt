package flank.corellium.cli

import flank.corellium.api.AndroidApps
import flank.corellium.api.AndroidInstance
import flank.corellium.api.Authorization
import flank.corellium.domain.RunTestCorelliumAndroid
import flank.corellium.domain.RunTestCorelliumAndroid.Args
import flank.corellium.domain.RunTestCorelliumAndroid.Authorize
import flank.corellium.domain.RunTestCorelliumAndroid.CleanUp
import flank.corellium.domain.RunTestCorelliumAndroid.CompleteTests
import flank.corellium.domain.RunTestCorelliumAndroid.DumpShards
import flank.corellium.domain.RunTestCorelliumAndroid.ExecuteTests
import flank.corellium.domain.RunTestCorelliumAndroid.GenerateReport
import flank.corellium.domain.RunTestCorelliumAndroid.InstallApks
import flank.corellium.domain.RunTestCorelliumAndroid.InvokeDevices
import flank.corellium.domain.RunTestCorelliumAndroid.LoadPreviousDurations
import flank.corellium.domain.RunTestCorelliumAndroid.OutputDir
import flank.corellium.domain.RunTestCorelliumAndroid.ParseApkInfo
import flank.corellium.domain.RunTestCorelliumAndroid.ParseTestCases
import flank.corellium.domain.RunTestCorelliumAndroid.PrepareShards
import flank.instrument.log.Instrument
import flank.log.Event
import flank.log.event
import flank.log.invoke
import flank.log.output
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
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
        gpuAcceleration = false
        scanPreviousDurations = 123
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
                "--gpu-acceleration=$gpuAcceleration",
                "--scan-previous-durations=$scanPreviousDurations",
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
gpu-acceleration: $gpuAcceleration
scan-previous-durations: $scanPreviousDurations
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
                obfuscateDumpShards = obfuscate!!,
                gpuAcceleration = gpuAcceleration!!,
                scanPreviousDurations = scanPreviousDurations!!,
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

    /**
     * Test is checking if all specified events have registered dedicated formatters.
     * Also, the specified events should be formatted and printed to console output.
     */
    @Test
    fun outputTest() {
        // ======================== GIVEN ========================

        val events = listOf(
            Authorize event Event.Start,
            CleanUp event Event.Start,
            OutputDir event Event.Start,
            DumpShards event Event.Start,
            ExecuteTests event Event.Start,
            CompleteTests event Event.Start,
            GenerateReport event Event.Start,
            InstallApks event Event.Start,
            InvokeDevices event Event.Start,
            LoadPreviousDurations event Event.Start,
            ParseApkInfo event Event.Start,
            ParseTestCases event Event.Start,
            PrepareShards event Event.Start,
            Unit event LoadPreviousDurations.Searching(5),
            Unit event LoadPreviousDurations.Summary(1, 2, 3),
            Unit event InstallApks.Status(AndroidApps.Event.Connecting.Agent("123456")),
            Unit event InstallApks.Status(AndroidApps.Event.Connecting.Console("123456")),
            Unit event InstallApks.Status(AndroidApps.Event.Apk.Uploading("path/to/apk.apk")),
            Unit event InstallApks.Status(AndroidApps.Event.Apk.Installing("path/to/apk.apk")),
            Unit event InvokeDevices.Status(AndroidInstance.Event.GettingAlreadyCreated),
            Unit event InvokeDevices.Status(AndroidInstance.Event.Obtained(5)),
            Unit event InvokeDevices.Status(AndroidInstance.Event.Starting(6)),
            Unit event InvokeDevices.Status(AndroidInstance.Event.Started("123456", "AndroidDevice")),
            Unit event InvokeDevices.Status(AndroidInstance.Event.Creating(7)),
            Unit event InvokeDevices.Status(AndroidInstance.Event.Waiting),
            Unit event InvokeDevices.Status(AndroidInstance.Event.Ready("123456")),
            Unit event ExecuteTests.Status(
                id = "123456",
                status = Instrument.Status(
                    code = 0,
                    startTime = 1,
                    endTime = 2,
                    details = Instrument.Status.Details(emptyMap(), "Class", "Test", null)
                )
            ),
            Unit event RunTestCorelliumAndroid.Created(File("path/to/apk.apk")),
            Unit event RunTestCorelliumAndroid.AlreadyExist(File("path/to/apk.apk")),
        )

        val printLog = format.output

        // ======================== WHEN ========================

        val nulls = events
            .onEach(printLog)
            .associateWith { format(it) }
            .filterValues { it == null }

        // ======================== THEN ========================

        assertTrue(
            nulls.keys.joinToString("\n", "Missing formatters for:\n"),
            nulls.isEmpty()
        )
    }
}
