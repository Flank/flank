package flank.corellium.shard

import java.nio.file.Files.newBufferedWriter
import java.nio.file.Paths.get

/**
 * Dump shards as formatted json file.
 *
 * @receiver List of shards to dump.
 * @param filePath Relative or absolut path the file.
 */
fun Shards.dumpToFile(
    filePath: String
) {
    val writer = newBufferedWriter(get(filePath))
    prettyGson.toJson(this, writer)
    writer.flush()
}
