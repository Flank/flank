package flank.corellium.domain

import flank.apk.Apk
import flank.corellium.api.AmInstrumentCommand
import flank.corellium.api.AndroidInstance
import flank.corellium.api.CorelliumApi
import flank.corellium.api.InstanceId
import flank.corellium.domain.TestAndroid.CompleteTests
import flank.corellium.domain.TestAndroid.execute
import flank.exection.parallel.Parallel
import flank.exection.parallel.invoke
import flank.exection.parallel.plus
import flank.exection.parallel.type
import flank.exection.parallel.validate
import flank.exection.parallel.verify
import flank.instrument.log.Instrument
import flank.junit.JUnit
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

class TestAndroidParsingTest {

    private val expectedShardsCount = 20

    private val args by lazy {
        TestAndroid.Args(
            credentials = stubCredentials,
            apks = manyTestArtifactsApks(APK_PATH_TEST),
            maxShardsCount = expectedShardsCount,
            outputDir = "test_results"
        )
    }

    private val initial = mapOf(
        TestAndroid.Args + args,

        type<CorelliumApi>() + CorelliumApi(
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
                    println("$key:\n" + value.joinToString("") { "$it\n" })
                }
                emptyFlow()
            },
            executeTest = { (instances: Map<InstanceId, List<AmInstrumentCommand>>) ->
                instances.forEach { (key, value) ->
                    println("$key:\n" + value.joinToString("") { "$it\n" })
                }
                instances.map { (id, commands) ->
                    id to flow {
                        commands.forEach { command ->
                            val testNames = command.split(" ")
                                .run { drop(indexOf("-e") + 2).first() }
                                .split(",")

                            testNames.forEachIndexed { index, name ->
                                val (className, testName) = name.split("#").run {
                                    first() to getOrNull(1)
                                }
                                produceInstrumentLog(
                                    current = index + 1,
                                    numTests = testNames.size,
                                    className = className,
                                    testName = testName ?: "test[0]",
                                    code = randomCode(),
                                ).lineSequence().forEach { emit(it) }
                            }

                            produceInstrumentResult(
                                time = 2.888f,
                                numTests = testNames.size
                            ).lineSequence().forEach { emit(it) }
                        }
                    }.buffer(Int.MAX_VALUE)
                }
            },
        ),

        Parallel.Logger + { println(this) },

        type<JUnit.Api>() + JUnit.Api(),

        type<Apk.Api>() + Apk.Api(),
    )

    @Test
    fun validate() {
        execute.validate(initial)
    }

    @Test
    fun test() {
        runBlocking { execute(CompleteTests)(initial).collect { state -> state.verify() } }
    }

    @After
    fun cleanUp() {
        File(args.outputDir).deleteRecursively()
    }
}

private fun randomCode(): Int = setOf(
    Instrument.Code.PASSED,
    Instrument.Code.FAILED,
    Instrument.Code.SKIPPED,
).random()

private fun produceInstrumentLog(
    current: Int,
    numTests: Int,
    testName: String,
    className: String,
    code: Int,
    stack: Throwable? = null,
) = """
INSTRUMENTATION_STATUS: class=$className
INSTRUMENTATION_STATUS: current=$current
INSTRUMENTATION_STATUS: id=AndroidJUnitRunner
INSTRUMENTATION_STATUS: numtests=$numTests
INSTRUMENTATION_STATUS: stream=
$className:
INSTRUMENTATION_STATUS: test=$testName
INSTRUMENTATION_STATUS_CODE: 1
INSTRUMENTATION_STATUS: class=$className
INSTRUMENTATION_STATUS: current=$current
INSTRUMENTATION_STATUS: id=AndroidJUnitRunner
INSTRUMENTATION_STATUS: numtests=$numTests
${stack.logStacktrace()}INSTRUMENTATION_STATUS: stream=
$className:
INSTRUMENTATION_STATUS: test=$testName
INSTRUMENTATION_STATUS_CODE: $code
""".trimIndent()

fun Throwable?.logStacktrace(): String = if (this == null) "" else
    "INSTRUMENTATION_STATUS: stack=${stackTraceToString()}\n"

private fun produceInstrumentResult(
    time: Float,
    numTests: Int,
    code: Int = -1,
) = """
INSTRUMENTATION_RESULT: stream=

Time: $time

OK ($numTests tests)


INSTRUMENTATION_CODE: $code
""".trimIndent()
