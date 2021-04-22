package flank.corellium.shard

import org.junit.Assert
import org.junit.Test as JTest

val apps = listOf(
    Shard.App(
        name = "app1",
        tests = listOf(
            Shard.Test(
                name = "app1-test1",
                cases = listOf(
                    Shard.Test.Case(
                        name = "class app1.test1.TestClass#test1",
                        duration = 1
                    ),
                )
            ),
        )
    ),
    Shard.App(
        name = "app2",
        tests = listOf(
            Shard.Test(
                name = "app2-test1",
                cases = listOf(
                    Shard.Test.Case(
                        name = "class app2.test1.TestClass#test2",
                        duration = 2
                    ),
                    Shard.Test.Case(
                        name = "class app2.test1.TestClass#test3",
                        duration = 3
                    ),
                )
            ),
            Shard.Test(
                name = "app2-test2",
                cases = listOf(
                    Shard.Test.Case(
                        name = "class app2.test2.TestClass#test7",
                        duration = 7
                    ),
                    Shard.Test.Case(
                        name = "class app2.test2.TestClass#test8",
                        duration = 8
                    ),
                    Shard.Test.Case(
                        name = "class app2.test2.TestClass#test9",
                        duration = 9
                    ),
                )
            ),
        )
    )
)

class ShardKtTest {

    @JTest
    fun test2() {
        apps.calculateShards(2).apply {
            printShards()
            verifyDurationEqual()
        }
    }

    @JTest
    fun test3() {
        apps.calculateShards(3).apply {
            printShards()
            verifyDurationEqual()
        }
    }
}

private fun List<List<Shard.App>>.printShards() {
    forEach {
        it.forEach {
            println(it.name)
            it.tests.forEach {
                println(it.name)
                it.cases.forEach {
                    println(it.name + ": " + it.duration)
                }
            }
        }
        println()
    }
}

private fun List<List<Shard.App>>.verifyDurationEqual() {
    map {
        it.sumByDouble {
            it.tests.sumByDouble {
                it.cases.sumByDouble {
                    it.duration.toDouble()
                }
            }
        }
    }.map {
        it.toLong()
    }.reduce { first, next ->
        Assert.assertEquals(first, next)
        first
    }
}
