package ftl.run.platform.android

import ftl.args.IArgs
import ftl.gc.GcStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

internal suspend fun IArgs.uploadOtherFiles(
    runGcsPath: String
): Map<String, String> = coroutineScope {
    otherFiles
        .map { (devicePath: String, filePath: String) ->
            async(Dispatchers.IO) { devicePath to GcStorage.upload(filePath, resultsBucket, runGcsPath) }
        }.awaitAll().toMap()
}

internal suspend fun AndroidArgs.uploadObbFiles(runGcsPath: String): Map<String, String> = coroutineScope {
    obbFiles.map {
        async(Dispatchers.IO) { it to GcStorage.upload(it, resultsBucket, runGcsPath) }
    }.awaitAll().toMap()
}
