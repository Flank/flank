package flank.corellium.domain.run.test.android.step

import flank.corellium.domain.RunTestCorelliumAndroid
import flank.corellium.domain.RunTestCorelliumAndroid.DumpShards
import flank.corellium.domain.RunTestCorelliumAndroid.OutputDir
import flank.corellium.domain.RunTestCorelliumAndroid.PrepareShards
import flank.corellium.domain.RunTestCorelliumAndroid.context
import flank.exection.parallel.from
import flank.exection.parallel.using
import flank.shard.dumpTo
import flank.shard.obfuscate
import java.io.File

/**
 * The step is saving calculated shards on drive to output directory.
 */
internal val dumpShards = DumpShards from setOf(
    PrepareShards,
    OutputDir,
) using context {
    val file = File(args.outputDir, ANDROID_SHARD_FILENAME)
    when (args.obfuscateDumpShards) {
        true -> obfuscate(shards)
        else -> shards
    } dumpTo file.writer()
    RunTestCorelliumAndroid.Created(file).out()
}

private const val ANDROID_SHARD_FILENAME = "android-shards.json"
