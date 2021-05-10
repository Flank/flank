package ftl.gc

import com.google.testing.model.AndroidDeviceList
import com.google.testing.model.TestSetup
import ftl.args.AndroidArgs
import ftl.client.google.GcToolResults.createToolResultsHistory
import ftl.gc.android.setEnvironmentVariables
import ftl.run.platform.android.AndroidTestConfig
import ftl.test.util.FlankTestRunner
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.StringReader

@RunWith(FlankTestRunner::class)
class GcAndroidTestMatrixTest {
    private val appApk = "../test_projects/android/apks/app-debug.apk"
    private val testApk = "../test_projects/android/apks/app-debug-androidTest.apk"

    @After
    fun tearDown() = unmockkAll()

    @Test(expected = IllegalArgumentException::class)
    fun `build negativeShardErrors`() {
        val androidArgs = mockk<AndroidArgs>(relaxed = true)

        GcAndroidTestMatrix.build(
            androidTestConfig = AndroidTestConfig.Instrumentation(
                appApkGcsPath = "",
                testApkGcsPath = "",
                testShards = emptyList(),
                orchestratorOption = null,
                numUniformShards = null,
                disableSharding = false,
                testRunnerClass = "",
                keepTestTargetsEmpty = false,
                testTargetsForShard = emptyList()
            ),
            runGcsPath = "",
            otherFiles = emptyMap(),
            androidDeviceList = AndroidDeviceList(),
            args = androidArgs,
            toolResultsHistory = createToolResultsHistory(androidArgs),
            additionalApkGcsPaths = emptyList(),
            obbFiles = emptyMap()
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `build invalidShardErrors`() {
        val androidArgs = mockk<AndroidArgs>(relaxed = true)

        GcAndroidTestMatrix.build(
            androidTestConfig = AndroidTestConfig.Instrumentation(
                appApkGcsPath = "",
                testApkGcsPath = "",
                testShards = listOf(listOf("")),
                orchestratorOption = null,
                numUniformShards = null,
                disableSharding = false,
                testRunnerClass = "",
                keepTestTargetsEmpty = false,
                testTargetsForShard = emptyList()
            ),
            runGcsPath = "",
            otherFiles = emptyMap(),
            androidDeviceList = AndroidDeviceList(),
            args = androidArgs,
            toolResultsHistory = createToolResultsHistory(androidArgs),
            additionalApkGcsPaths = emptyList(),
            obbFiles = emptyMap()
        )
    }

    @Test
    fun `build validArgs`() {
        val androidArgs = mockk<AndroidArgs>(relaxed = true)

        every { androidArgs.testTimeout } returns "3m"
        every { androidArgs.resultsBucket } returns "/hi"
        every { androidArgs.project } returns "123"

        GcAndroidTestMatrix.build(
            androidTestConfig = AndroidTestConfig.Instrumentation(
                appApkGcsPath = "",
                testApkGcsPath = "",
                testShards = emptyList(),
                orchestratorOption = null,
                numUniformShards = null,
                disableSharding = false,
                testRunnerClass = "",
                keepTestTargetsEmpty = false,
                testTargetsForShard = emptyList()
            ),
            runGcsPath = "",
            otherFiles = emptyMap(),
            androidDeviceList = AndroidDeviceList(),
            args = androidArgs,
            toolResultsHistory = createToolResultsHistory(androidArgs),
            additionalApkGcsPaths = emptyList(),
            obbFiles = emptyMap()
        )
    }

    @Test
    fun `should not set env variables on robo test`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          robo-directives:
            "click:button3": ""
            "ignore:button1": ""
            "ignore:button2": ""
          environment-variables:
            coverage: true
            coverageFilePath: /sdcard/
            clearPackageData: true
        """.trimIndent()
        val androidArgs = AndroidArgs.load(StringReader(yaml), null)

        val testSetup = TestSetup().setEnvironmentVariables(
            androidArgs,
            AndroidTestConfig.Robo(
                appApkGcsPath = "",
                flankRoboDirectives = emptyList(),
                roboScriptGcsPath = ""
            )
        )
        assertTrue(testSetup.environmentVariables.isEmpty())
    }

    @Test
    fun `should set env variables on instrumental test`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          robo-directives:
            "click:button3": ""
            "ignore:button1": ""
            "ignore:button2": ""
          environment-variables:
            coverage: true
            coverageFilePath: /sdcard/
            clearPackageData: true
        """.trimIndent()
        val androidArgs = AndroidArgs.load(StringReader(yaml), null)

        val testSetup = TestSetup().setEnvironmentVariables(
            androidArgs,
            AndroidTestConfig.Instrumentation(
                appApkGcsPath = "",
                testApkGcsPath = "",
                testShards = emptyList(),
                orchestratorOption = null,
                numUniformShards = null,
                disableSharding = false,
                testRunnerClass = "",
                keepTestTargetsEmpty = false,
                testTargetsForShard = emptyList()
            )
        )
        assertTrue(testSetup.environmentVariables.isNotEmpty())
    }

    @Test
    fun `should set env variables with values from test config`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          environment-variables:
            coverage: true
            coverageFilePath: /sdcard/
            clearPackageData: true
        """.trimIndent()
        val androidArgs = AndroidArgs.load(StringReader(yaml), null)

        val testSetup = TestSetup().setEnvironmentVariables(
            androidArgs,
            AndroidTestConfig.Instrumentation(
                appApkGcsPath = "",
                testApkGcsPath = "",
                testShards = emptyList(),
                orchestratorOption = null,
                numUniformShards = null,
                disableSharding = false,
                testRunnerClass = "",
                keepTestTargetsEmpty = false,
                environmentVariables = mapOf(Pair("coverageFile", "/sdcard/test.ec")),
                testTargetsForShard = emptyList()
            )
        )
        assertTrue(testSetup.environmentVariables.any { it.key == "coverageFile" && it.value == "/sdcard/test.ec" })
    }

    @Test
    fun `should set env variables and override androidArgs env variables by value from TestConfig variables`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          environment-variables:
            coverage: true
            coverageFile: /sdcard/test.ec
            clearPackageData: true
        """.trimIndent()
        val androidArgs = AndroidArgs.load(StringReader(yaml), null)

        val testSetup = TestSetup().setEnvironmentVariables(
            androidArgs,
            AndroidTestConfig.Instrumentation(
                appApkGcsPath = "",
                testApkGcsPath = "",
                testShards = emptyList(),
                orchestratorOption = null,
                numUniformShards = null,
                disableSharding = false,
                testRunnerClass = "",
                keepTestTargetsEmpty = false,
                environmentVariables = mapOf(Pair("coverageFile", "/sdcard/test_module1.ec")),
                testTargetsForShard = emptyList()
            )
        )
        assertTrue(testSetup.environmentVariables.any { it.key == "coverageFile" && it.value == "/sdcard/test_module1.ec" })
    }

    @Test
    fun `should override env variable value instead of add second with same key`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          environment-variables:
            coverage: true
            coverageFile: /sdcard/test.ec
            clearPackageData: true
        """.trimIndent()
        val androidArgs = AndroidArgs.load(StringReader(yaml), null)

        val testSetup = TestSetup().setEnvironmentVariables(
            androidArgs,
            AndroidTestConfig.Instrumentation(
                appApkGcsPath = "",
                testApkGcsPath = "",
                testShards = emptyList(),
                orchestratorOption = null,
                numUniformShards = null,
                disableSharding = false,
                testRunnerClass = "",
                keepTestTargetsEmpty = false,
                environmentVariables = mapOf(Pair("coverageFile", "/sdcard/test_module1.ec")),
                testTargetsForShard = emptyList()
            )
        )
        assertEquals(1, testSetup.environmentVariables.count { it.key == "coverageFile" })
    }
}
