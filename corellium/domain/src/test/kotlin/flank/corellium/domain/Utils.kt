package flank.corellium.domain

import flank.corellium.api.Authorization
import flank.corellium.domain.RunTestCorelliumAndroid.Args
import kotlinx.coroutines.Job
import java.util.Properties

const val APK_PATH = "test_artifacts/master/apk"
const val APK_PATH_TEST = "../../$APK_PATH"
const val APK_PATH_MAIN = "./$APK_PATH"

val loadedCredentials by lazy {
    Properties().run {
        load(Unit.javaClass.classLoader.getResourceAsStream("corellium.properties"))

        Authorization.Credentials(
            host = getProperty("api"),
            username = getProperty("username"),
            password = getProperty("password"),
        )
    }
}

val stubCredentials = Authorization.Credentials(
    host = "host",
    username = "username",
    password = "password"
)

fun manyTestArtifactsApks(apkPath: String) = listOf(
    Args.Apk.App(
        path = "$apkPath/app-debug.apk",
        tests = listOf(
            "app-multiple-error-debug-androidTest.apk",
            "app-single-success-debug-androidTest.apk",
        ).map { Args.Apk.Test("$apkPath/$it") }
    ),
    Args.Apk.App(
        path = "$apkPath/app_many_tests-debug.apk",
        tests = listOf(
            "app_many_tests-debug-androidTest.apk",
        ).map { Args.Apk.Test("$apkPath/$it") }
    ),
)

fun fewTestArtifactsApks(apkPath: String) = listOf(
    Args.Apk.App(
        path = "$apkPath/app-debug.apk",
        tests = listOf(
            "app-multiple-error-debug-androidTest.apk",
        ).map { Args.Apk.Test("$apkPath/$it") }
    )
)

val completeJob = Job().apply {
    complete()
}
