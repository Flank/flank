package ftl.run.platform.android

import ftl.args.AndroidArgs
import ftl.args.yml.ResolvedApks
import ftl.args.yml.UploadedApks
import ftl.gc.GcStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

/**
 * Upload an APK pair if the path given is local
 *
 * @return AppTestPair with their GCS paths
 */
internal suspend fun uploadApks(
    apks: ResolvedApks,
    args: AndroidArgs,
    runGcsPath: String
): UploadedApks = coroutineScope {
    val gcsBucket = args.resultsBucket

    val appApkGcsPath = async(Dispatchers.IO) { GcStorage.upload(apks.app, gcsBucket, runGcsPath) }
    val testApkGcsPath = apks.test?.let { async(Dispatchers.IO) { GcStorage.upload(it, gcsBucket, runGcsPath) } }
    val additionalApkGcsPaths =
        apks.additionalApks.map { async(Dispatchers.IO) { GcStorage.upload(it, gcsBucket, runGcsPath) } }

    UploadedApks(
        app = appApkGcsPath.await(),
        test = testApkGcsPath?.await(),
        additionalApks = additionalApkGcsPaths.awaitAll()
    )
}
