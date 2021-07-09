package flank.corellium.domain

import flank.apk.Apk
import flank.corellium.api.CorelliumApi
import flank.corellium.corelliumApi
import flank.corellium.domain.RunTestCorelliumAndroid.Args
import flank.corellium.domain.RunTestCorelliumAndroid.CompleteTests
import flank.corellium.domain.RunTestCorelliumAndroid.execute
import flank.exection.parallel.Parallel
import flank.exection.parallel.invoke
import flank.exection.parallel.type
import flank.junit.JUnit
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking

fun main() {
    val args = mapOf(
        type<CorelliumApi>() to corelliumApi("Default Project"),
        type<Apk.Api>() to Apk.Api(),
        type<JUnit.Api>() to JUnit.Api(),
        Args to Args(
            credentials = loadedCredentials,
            apks = fewTestArtifactsApks(APK_PATH_MAIN),
            maxShardsCount = 3
        ),
        Parallel.Logger to fun Any.() = println(this)
    )

    runBlocking { execute(CompleteTests)(args).collect() }
}
