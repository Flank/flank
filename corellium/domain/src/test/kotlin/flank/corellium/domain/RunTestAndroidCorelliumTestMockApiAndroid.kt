package flank.corellium.domain

import flank.apk.Apk
import flank.corellium.api.CorelliumApi
import flank.corellium.domain.RunTestCorelliumAndroid.Args
import flank.junit.JUnit
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

class RunTestAndroidCorelliumTestMockApiAndroid : RunTestCorelliumAndroid.Context {

    private val expectedShardsCount = 4

    override val args = Args(
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

    override val api = CorelliumApi(
        authorize = { credentials ->
            println(credentials)
            assertEquals(args.credentials, credentials)
            completeJob
        },
        invokeAndroidDevices = { (amount) ->
            assertEquals(expectedShardsCount, amount)
            (1..amount).asFlow().map(Int::toString)
        },
        installAndroidApps = { list ->
            println(list)
            assertEquals(expectedShardsCount, list.size)
            completeJob
        },
        executeTest = { (instances) ->
            println(instances)
            assertEquals(expectedShardsCount, instances.size)
            emptyList()
        },
    )

    override val apk = Apk.Api(
        parseTestCases = { path ->
            println(path)
            (1..path.last().toString().toInt()).map {
                path.replace("/", ".") + ".Test#test$it"
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
    )

    override val junit = JUnit.Api()

    @Test
    fun test(): Unit = invoke()

    @After
    fun cleanUp() {
        File(args.outputDir).deleteRecursively()
    }
}
