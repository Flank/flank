package flank.corellium.domain

import flank.apk.Apk
import flank.corellium.api.AndroidInstance
import flank.corellium.api.CorelliumApi
import flank.corellium.domain.TestAndroid.Args
import flank.corellium.domain.TestAndroid.CompleteTests
import flank.corellium.domain.TestAndroid.execute
import flank.exection.parallel.Parallel
import flank.exection.parallel.invoke
import flank.exection.parallel.type
import flank.exection.parallel.validate
import flank.exection.parallel.verify
import flank.junit.JUnit
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

class TestAndroidMockApiTest {

    private val expectedShardsCount = 4

    private val args = Args(
        credentials = stubCredentials,
        apks = listOf(
            Args.Apk.App(
                path = "app1",
                tests = listOf(
                    Args.Apk.Test("app1/test1"),
                    Args.Apk.Test("app1/test2"),
                    Args.Apk.Test("app1/test3"),
                )
            ),
            Args.Apk.App(
                path = "app2",
                tests = listOf(
                    Args.Apk.Test("app2/test1"),
                    Args.Apk.Test("app2/test2")
                )
            ),
            Args.Apk.App(
                path = "app3",
                tests = listOf(
                    Args.Apk.Test("app3/test1")
                )
            ),
        ),
        maxShardsCount = expectedShardsCount
    )

    private val initial = mapOf(
        Args to args,

        type<CorelliumApi>() to CorelliumApi(
            authorize = { credentials ->
                println(credentials)
                assertEquals(args.credentials, credentials)
                completeJob
            },
            invokeAndroidDevices = { (amount) ->
                assertEquals(expectedShardsCount, amount)
                (1..amount).asFlow().map {
                    AndroidInstance.Event.Ready(it.toString())
                }
            },
            installAndroidApps = { list ->
                println(list)
                emptyFlow()
            },
            executeTest = { (instances) ->
                println(instances)
                emptyList()
            },
            getRate = {
                GlobalScope.async {
                    AndroidInstance.RateInfo(10_000_000, 1_000_000)
                }
            }
        ),

        type<Apk.Api>() to Apk.Api(
            parseTestCases = {
                { path ->
                    println(path)
                    (1..path.last().toString().toInt()).map {
                        path.replace("/", ".") + ".Test#test$it"
                    }
                }
            },
            parsePackageName = { path ->
                println(path)
                path
            },
            parseInfo = { path ->
                println(path)
                Apk.Info(
                    packageName = path,
                    testRunner = path
                )
            },
        ),

        type<JUnit.Api>() to JUnit.Api(),

        Parallel.Logger to fun Any.() = println(this),
    )

    @Test
    fun validate() {
        execute.validate(initial)
    }

    @Test
    fun test() {
        runBlocking { execute(CompleteTests)(initial).collect { it.verify() } }
    }

    @After
    fun cleanUp() {
        File(args.outputDir).deleteRecursively()
    }
}
