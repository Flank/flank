package flank.corellium.domain

import flank.apk.Apk
import flank.corellium.api.AndroidInstance
import flank.corellium.api.CorelliumApi
import flank.corellium.domain.RunTestCorelliumAndroid.CompleteTests
import flank.corellium.domain.RunTestCorelliumAndroid.execute
import flank.exection.parallel.Parallel
import flank.exection.parallel.invoke
import flank.exection.parallel.type
import flank.exection.parallel.validate
import flank.junit.JUnit
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

class RunTestAndroidCorelliumTestParsingAndroid {

    private val expectedShardsCount = 20

    private val args by lazy {
        RunTestCorelliumAndroid.Args(
            credentials = stubCredentials,
            apks = manyTestArtifactsApks(APK_PATH_TEST),
            maxShardsCount = expectedShardsCount,
            outputDir = "test_results"
        )
    }

    private val initial = mapOf(
        RunTestCorelliumAndroid.Args to args,

        type<CorelliumApi>() to CorelliumApi(
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
        ),

        Parallel.Logger to fun Any.() = println(this),

        type<JUnit.Api>() to JUnit.Api(),

        type<Apk.Api>() to Apk.Api(),
    )

    @Test
    fun validate() {
        execute.validate(initial)
    }

    @Test
    fun test() {
        runBlocking { execute(CompleteTests)(initial).collect() }
    }

    @After
    fun cleanUp() {
        File(args.outputDir).deleteRecursively()
    }
}
