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
