package flank.corellium.domain

import flank.apk.Apk
import flank.corellium.api.AndroidInstance
import flank.corellium.api.CorelliumApi
import flank.log.Output
import flank.junit.JUnit
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emptyFlow
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
        authorize = { credentials ->
            println(credentials)
            assertEquals(args.credentials, credentials)
            completeJob
        },
        invokeAndroidDevices = { (amount) ->
            println(amount)
            assertEquals(expectedShardsCount, amount)
            (1..amount).asFlow().map {
                AndroidInstance.Event.Ready(it.toString())
            }
        },
        installAndroidApps = { apps ->
            apps.forEach { (key, value) ->
                println("$key:")
                value.forEach { path ->
                    println(path)
                }
            }
            assertEquals(expectedShardsCount, apps.size)
            emptyFlow()
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

    override val out: Output = { }

    override val junit = JUnit.Api()

    override val apk = Apk.Api()

    @Test
    fun test(): Unit = invoke()

    @After
    fun cleanUp() {
        File(args.outputDir).deleteRecursively()
    }
}
