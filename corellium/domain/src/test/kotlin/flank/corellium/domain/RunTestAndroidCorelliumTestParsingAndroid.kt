package flank.corellium.domain

import flank.corellium.adapter.parseApkInfo
import flank.corellium.adapter.parseApkPackageName
import flank.corellium.adapter.parseApkTestCases
import flank.corellium.api.CorelliumApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

class RunTestAndroidCorelliumTestParsingAndroid : RunTestCorelliumAndroid.Context {

    private val expectedShardsCount = 20

    override val args by lazy {
        RunTestCorelliumAndroid.Args(
            credentials = stubCredentials,
            apks = manyTestArtifactsApks(APK_PATH_TEST),
            maxShardsCount = expectedShardsCount,
            outputDir = "test_results"
        )
    }

    override val api = CorelliumApi(
        parseTestCases = parseApkTestCases,
        parsePackageName = parseApkPackageName,
        parseTestApkInfo = parseApkInfo,
        authorize = { credentials ->
            println(credentials)
            assertEquals(args.credentials, credentials)
            completeJob
        },
        invokeAndroidDevices = { (amount) ->
            println(amount)
            assertEquals(expectedShardsCount, amount)
            (1..amount).asFlow().map(Int::toString)
        },
        installAndroidApps = { apps ->
            apps.forEach { (key, value) ->
                println("$key:")
                value.forEach { path ->
                    println(path)
                }
            }
            assertEquals(expectedShardsCount, apps.size)
            completeJob
        },
        executeTest = { (instances) ->
            instances.forEach { (key, value) ->
                println("$key:")
                value.forEach { shard ->
                    println(shard)
                }
            }
            assertEquals(expectedShardsCount, instances.size)
            emptyList()
        },
    )

    @Test
    fun test(): Unit = invoke()

    @After
    fun cleanUp() {
        File(args.outputDir).deleteRecursively()
    }
}
