package ftl

import ftl.Utils.fatalError
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Parallel(shardCount: Int) {
    private val callables: MutableList<Callable<Any>>
    private val executorService: ExecutorService

    init {
        callables = ArrayList(shardCount)
        executorService = Executors.newFixedThreadPool(shardCount)
    }

    fun addCallable(callable: Callable<Any>) {
        callables.add(callable)
    }

    fun run() {
        try {
            executorService
                    .invokeAll(callables)
                    .stream()
                    .map<Any> { future ->
                        future.get()
                    }
        } catch (e: InterruptedException) {
            fatalError(e)
        } finally {
            executorService.shutdown()
        }
    }
}
