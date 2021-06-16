package flank.corellium.domain.run.test.android.step

import flank.corellium.domain.RunTestCorelliumAndroid
import flank.corellium.domain.RunTestCorelliumAndroid.DumpShards
import flank.corellium.domain.step
import flank.shard.dumpTo
import flank.shard.obfuscate
import java.io.File

/**
 * The step is saving calculated shards on drive to output directory.
 *
 * require:
 * * [RunTestCorelliumAndroid.Context.prepareShards]
 * * [RunTestCorelliumAndroid.Context.createOutputDir]
 */

internal fun RunTestCorelliumAndroid.Context.dumpShards() = step(DumpShards) { out ->
    val file = File(args.outputDir, ANDROID_SHARD_FILENAME)
    when (args.obfuscateDumpShards) {
        true -> obfuscate(shards)
        else -> shards
    } dumpTo file.writer()
    RunTestCorelliumAndroid.Created(file).out()
    this
}

private const val ANDROID_SHARD_FILENAME = "android-shards.json"
