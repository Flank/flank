package flank.exection.parallel

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

object A : Parallel.Type<Int>
object B : Parallel.Type<Int>
object C : Parallel.Type<Int>
object WaitTime : Parallel.Type<Long>
object Sum : Parallel.Type<Int>
object Delayed : Parallel.Type<Unit>
object Failed : Parallel.Type<Nothing>
object NotRunning : Parallel.Type<Unit>

private class Context : Parallel.Context() {
    val wait by WaitTime()
    val a by A()
    val b by B()
    val c by C()
    val sum by Sum()
}

private val async = Parallel.Task.Body(::Context)

private val execute = setOf(

    A using { 1 },

    B using { 2 },

    C using { 3 },

    Delayed using async { delay(wait) },

    Failed from setOf(Delayed, Sum) using async { throw Exception() },

    Sum from setOf(A, B, C) using async { a + b + c },

    NotRunning from setOf(Failed) using { }

)

private val tasks = execute.associateBy { it.signature.returns }

private val initial: ParallelState = mapOf(WaitTime to 300)

class ExecutionKtTest {

    @Test
    fun test1() {
        val result = runBlocking { execute(initial).last() }
        Assert.assertEquals(1, result[A])
        Assert.assertEquals(2, result[B])
        Assert.assertEquals(3, result[C])
        Assert.assertEquals(6, result[Sum])
        Assert.assertEquals(Unit, result[Delayed])
        Assert.assertTrue(result[Failed] is Exception)
        Assert.assertEquals(execute.last(), result[NotRunning])
    }

    @Test
    fun test2() {
        runBlocking { (tasks - A).values.toSet()(Sum)(initial).last() }
    }
}
