package flank.corellium.adapter

import flank.corellium.api.AndroidApps
import flank.corellium.api.AndroidInstance
import flank.corellium.api.AndroidTestPlan
import flank.corellium.api.Authorization
import flank.corellium.api.invoke
import flank.corellium.corelliumApi
import java.util.Properties
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking

private const val PROJECT_NAME = "Default Project"

val credentials = Properties().run {
    load(Unit.javaClass.classLoader.getResourceAsStream("corellium.properties"))

    Authorization.Credentials(
        host = getProperty("api"),
        username = getProperty("username"),
        password = getProperty("password"),
    )
}

private val shardsForTestPlan = listOf(
    listOf(
        AndroidTestPlan.Shard(
            packageName = "com.example.test_app.test",
            testRunner = "androidx.test.runner.AndroidJUnitRunner",
            testCases = listOf(
                "class com.example.test_app.InstrumentedTest#test0",
                "class com.example.test_app.InstrumentedTest#test1",
            )
        ),
        AndroidTestPlan.Shard(
            packageName = "com.example.test_app.test",
            testRunner = "androidx.test.runner.AndroidJUnitRunner",
            testCases = listOf(
                "class com.example.test_app.InstrumentedTest#test2",
            )
        ),
    ),
    listOf(
        AndroidTestPlan.Shard(
            packageName = "com.example.test_app.test",
            testRunner = "androidx.test.runner.AndroidJUnitRunner",
            testCases = listOf(
                "class com.example.test_app.foo.FooInstrumentedTest#testFoo",
            )
        )
    ),
)

private val apks = listOf(
    "./test_artifacts/master/apk/app-debug.apk",
    "./test_artifacts/master/apk/app-multiple-success-debug-androidTest.apk",
)

fun main() {
    val api = corelliumApi(PROJECT_NAME)

    runBlocking {

        println("* Authorizing")
        api.authorize(credentials)

        println("* Invoking devices")
        val ids = api.invokeAndroidDevices(AndroidInstance.Config(2)).toMutableList()

        println("* Installing apks")
        val apps = ids.map { id -> AndroidApps(id, apks) }
        api.installAndroidApps(apps)

        // If tests will be executed to fast just after the
        // app installed, the instrumentation will fail
        //
        delay(10_000)

        // Update test plan device ids
        val shards = shardsForTestPlan.toMutableList()
        val testPlan = AndroidTestPlan.Config(
            instances = ids.associateWith {
                shards.removeFirst()
            }
        )

        println("* Executing tests")
        api.executeTest(testPlan).collect { line ->
            print(line)
        }
        println()
        println("* Finish")
    }
}
