package flank.corellium.domain

import flank.corellium.api.Apk
import flank.corellium.api.CorelliumApi
import flank.corellium.domain.RunTestCorelliumAndroid.Args
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
        parseTestCases = { path ->
            println(path)
            (1..path.last().toString().toInt()).map {
                path.replace("/", ".") + ".Test#test$it"
            }
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
        parsePackageName = { path ->
            println(path)
            path
        },
        parseTestApkInfo = { path ->
            println(path)
            Apk.Info(
                packageName = path,
                testRunner = path
            )
        },
        executeTest = { (instances) ->
            println(instances)
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
