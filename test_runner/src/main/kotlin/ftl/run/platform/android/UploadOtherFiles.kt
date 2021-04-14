package ftl.run.platform.android

import ftl.adapter.google.GcStorage
import ftl.args.AndroidArgs
import ftl.args.IArgs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

internal suspend fun IArgs.uploadOtherFiles(): Map<String, String> = coroutineScope {
    otherFiles
        .map { (devicePath: String, filePath: String) ->
            async(Dispatchers.IO) { devicePath to GcStorage.upload(filePath, resultsBucket, resultsDir) }
        }.awaitAll().toMap()
}

internal suspend fun AndroidArgs.uploadObbFiles(): Map<String, String> = coroutineScope {
    obbFiles.map {
        async(Dispatchers.IO) { it to GcStorage.upload(it, resultsBucket, resultsDir) }
    }.awaitAll().toMap()
}
