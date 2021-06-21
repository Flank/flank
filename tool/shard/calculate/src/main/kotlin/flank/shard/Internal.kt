package flank.shard

import kotlin.math.min

// Stage 1

/**
 * Create flat and easy to iterate list of [Chunk] without losing info about app and test related to test case.
 * @receiver The [List] of [Shard.App].
 * @return The [List] of [Chunk] which is just different representation of input data.
 */
internal fun List<Shard.App>.mapToInternalChunks(): List<Chunk> =
    mutableListOf<Chunk>().also { result ->
        forEach { app ->
            app.tests.forEach { test ->
                test.cases.forEach { case ->
                    result.add(
                        Chunk(
                            app = app.name,
                            test = test.name,
                            case = case.name,
                            duration = case.duration
                        )
                    )
                }
            }
        }
    }

/**
 * Internal intermediate structure which is representing test case with associated app and test module.
 */
internal class Chunk(
    val app: String,
    val test: String,
    val case: String,
    val duration: Long,
)

// Stage 2

/**
 * Group the chunks into sub-lists where the standard deviation of group duration should by possibly small.
 * @receiver The flat [List] of [Chunk].
 * @return The [List] of [Chunk] groups balanced by the summary duration of each.
 */
internal fun List<Chunk>.groupByDuration(maxCount: Int): List<List<Chunk>> {
    class Chunks(
        var duration: Long = 0,
        val list: MutableList<Chunk> = mutableListOf()
    )

    // The real part of sharding calculations,
    // everything else is just a mapping between structures.
    // ===================================================
    return sortedByDescending(Chunk::duration).fold(
        initial = List(min(size, maxCount)) { Chunks() }
    ) { acc, chunk ->
        acc.first().apply {
            duration += chunk.duration
            list += chunk
        }
        acc.sortedBy(Chunks::duration)
    }.map(Chunks::list)
    // ===================================================
}

// Stage 3

/**
 * Build the final structure of shards.
 */
internal fun List<List<Chunk>>.mapToShards(): List<List<Shard.App>> {

    // Structure the group of chunks mapping them by app and test.
    val list: List<Map<String, Map<String, List<Chunk>>>> = map { group ->
        group.groupBy { chunk ->
            chunk.app
        }.mapValues { (_, chunks) ->
            chunks.groupBy { chunk ->
                chunk.test
            }
        }
    }

    // Convert grouped chunks into the output structures.
    return list.map { map ->
        map.map { (app, tests) ->
            Shard.App(
                name = app,
                tests = tests.map { (test, chunks) ->
                    Shard.Test(
                        name = test,
                        cases = chunks.map { chunk ->
                            Shard.Test.Case(
                                name = chunk.case,
                                duration = chunk.duration
                            )
                        }
                    )
                }
            )
        }
    }
}
