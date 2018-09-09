package ftl.util

import java.util.concurrent.atomic.AtomicInteger

class ShardCounter {
    private val matrixShardIndex = AtomicInteger(0)
    fun next() = "shard_${matrixShardIndex.getAndIncrement()}"
}
