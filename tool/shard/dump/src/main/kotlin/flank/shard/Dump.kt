package flank.shard

import flank.shard.mapper.prettyGson
import java.io.Writer

/**
 * Dump shards as json formatted string.
 *
 * @receiver List of shards to dump.
 * @param writer Writer that will receive formatted json.
 */
infix fun Shards.dumpTo(
    writer: Writer
) {
    prettyGson.toJson(this, writer)
    writer.flush()
}
