package flank.corellium.cli.test.android.task

import flank.corellium.api.Authorization
import flank.corellium.cli.TestAndroidCommand
import flank.corellium.domain.TestAndroid.Args
import flank.exection.parallel.invoke
import flank.exection.parallel.select
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import java.io.File

class ArgsKtTest {

    /**
     * The test is checking if args [TestAndroidCommand.args] are generated correctly, basing on the given [TestAndroidCommand.Config]
     */
    @Test
    fun test() {
        // ======================== GIVEN ========================

        val testConfig = TestAndroidCommand.Config().applyTestValues()

        val expectedCredentials = Authorization.Credentials(
            host = "api.host.io",
            username = "user",
            password = "pass",
        )

        val expected = testConfig.run {
            Args(
                credentials = expectedCredentials,
                apks = apks!!,
                testTargets = testTargets!!,
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

        val actual = runBlocking {
            setOf(args)(TestAndroidCommand.Config to testConfig).last().select(Args)
        }

        // ======================== THEN ========================

        Assert.assertEquals(expected, actual)
    }
}
