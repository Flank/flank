package flank.execution.parallel.internal.graph

import org.junit.Assert.assertEquals
import org.junit.Test

class FindCyclesKtTest {

    @Test
    fun valid0() {
        val graph = mapOf(
            0 to setOf(1, 2),
            1 to setOf(3),
            2 to setOf(3),
            3 to setOf(),
        )

        val actual = graph.findCycles()

        assertEquals(emptyList<Any>(), actual)
    }

    @Test
    fun validRandom0() = repeat(100) {
        val nodes = (0..100)
        val used = mutableSetOf<Int>()
        val graph = nodes.associateWith {
            used += it
            (nodes.shuffled() - used).run { take((0..(size / 10)).random()) }.toSet()
        }
        // println("=================================")
        // graph.forEach { println(it) }
        // println()
        val actual = graph.findCycles()

        assertEquals(emptyList<Any>(), actual)
    }

    @Test
    fun cyclic0() {
        val graph = mapOf(
            0 to setOf(0)
        )
        val expected = listOf(listOf(0))

        val actual = graph.findCycles()

        assertEquals(expected, actual)
    }

    @Test
    fun cyclic1() {
        val graph = mapOf(
            0 to setOf(1),
            1 to setOf(0),
        )
        val expected = listOf(
            listOf(0, 1, 0)
        )
        val actual = graph.findCycles()

        assertEquals(expected, actual)
    }

    @Test
    fun cyclic2() {
        val graph = mapOf(
            0 to setOf(1),
            1 to setOf(2),
            2 to setOf(0),
        )
        val expected = listOf(
            listOf(0, 1, 2, 0)
        )
        val actual = graph.findCycles()

        assertEquals(expected, actual)
    }

    @Test
    fun cyclic3() {
        val graph = mapOf(
            0 to setOf(1),
            1 to setOf(2),
            2 to setOf(3),
            3 to setOf(0),
        )
        val expected = listOf(
            listOf(0, 1, 2, 3, 0)
        )
        val actual = graph.findCycles()

        assertEquals(expected, actual)
    }

    @Test
    fun cyclic4() {
        val graph = mapOf(
            0 to setOf(1),
            1 to setOf(2),
            2 to setOf(3),
            3 to setOf(1),
        )
        val expected = listOf(
            listOf(0, 1, 2, 3, 1)
        )
        val actual = graph.findCycles()

        assertEquals(expected, actual)
    }

    @Test
    fun cyclic5() {
        val graph = mapOf(
            0 to setOf(1),
            1 to setOf(2),
            2 to setOf(3),
            3 to setOf(1, 4),
            4 to setOf(5),
            5 to setOf(3)
        )
        val expected = listOf(
            listOf(3, 1, 2, 3),
            listOf(3, 4, 5, 3),
        )
        val actual = graph.findCycles()

        assertEquals(expected, actual)
    }
}
