package flank.corellium.domain.test.android.task

import flank.corellium.domain.TestAndroid
import flank.corellium.domain.TestAndroid.DumpShards
import flank.corellium.domain.TestAndroid.OutputDir
import flank.corellium.domain.TestAndroid.PrepareShards
import flank.corellium.domain.TestAndroid.context
import flank.exection.parallel.from
import flank.exection.parallel.using
import flank.shard.dumpTo
import flank.shard.obfuscate
import java.io.File

/**
 * Saves calculated shards on the drive into the output directory specific for a testing platform.
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
    TestAndroid.Created(file).out()
}

private const val ANDROID_SHARD_FILENAME = "android-shards.json"
