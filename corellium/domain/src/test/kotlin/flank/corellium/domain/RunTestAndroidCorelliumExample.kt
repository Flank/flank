package flank.corellium.domain

import flank.corellium.corelliumApi

object RunTestAndroidCorelliumExample : RunTestCorelliumAndroid.Context {
    override val api = corelliumApi("Default Project")
    override val args = RunTestCorelliumAndroid.Args(
        credentials = loadedCredentials,
        apks = fewTestArtifactsApks(APK_PATH_MAIN),
        maxShardsCount = 3
    )
}

fun main() = RunTestAndroidCorelliumExample()
