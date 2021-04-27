package flank.corellium.adapter

import flank.corellium.api.AndroidTestPlan
import flank.corellium.api.Authorization
import flank.corellium.api.invoke
import flank.corellium.client.core.getAllProjects
import flank.corellium.client.core.getProjectInstancesList
import flank.corellium.corelliumApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking

private const val PROJECT_NAME = "Default Project"

// The credentials are not provided along with the code.
// If you need to execute example you have to deliver credentials on your own.
private val credentials = Authorization.Credentials(
    host = TODO(),
    username = TODO(),
    password = TODO(),
)

private val androidTestPlanConfig = AndroidTestPlan.Config(
    instances = mapOf(
        "d8ae09fe-a60a-480a-968f-1a30d77a0e11" to listOf(
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
        "b7a305e5-b199-4ed6-9ba7-4c9b83c6762e" to listOf(
            AndroidTestPlan.Shard(
                packageName = "com.example.test_app.test",
                testRunner = "androidx.test.runner.AndroidJUnitRunner",
                testCases = listOf(
                    "class com.example.test_app.foo.FooInstrumentedTest#testFoo",
                )
            )
        )
    )
)

fun main() {
    val api = corelliumApi(PROJECT_NAME)

    runBlocking {

        println("* Authorizing")
        api.authorize(credentials)

        corellium.run {
            getProjectInstancesList(getAllProjects().first { it.name == PROJECT_NAME }.id)
        }.forEach {
            println(it)
        }

        println("* Executing tests")
        api.executeTest(androidTestPlanConfig).collect { line ->
            print(line)
        }
        println()
        println("* Finish")
    }
}
