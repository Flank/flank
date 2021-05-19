package flank.corellium.domain

import flank.apk.Apk
import flank.corellium.corelliumApi

object RunTestAndroidCorelliumExample : RunTestCorelliumAndroid.Context {
    override val api = corelliumApi("Default Project")
    override val apk = Apk.Api()
    override val args = RunTestCorelliumAndroid.Args(
        credentials = loadedCredentials,
        apks = fewTestArtifactsApks(APK_PATH_MAIN),
        maxShardsCount = 3
    )
}

fun main() = RunTestAndroidCorelliumExample()
