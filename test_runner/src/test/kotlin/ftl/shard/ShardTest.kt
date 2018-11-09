package ftl.shard

import com.google.common.truth.Truth.assertThat
import ftl.test.util.FlankTestRunner
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(FlankTestRunner::class)
class ShardTest {

    private val a1 = TestMethod("a", 1.0)
    private val b2 = TestMethod("b", 2.0)
    private val c3 = TestMethod("c", 3.0)
    private val d4 = TestMethod("d", 4.0)
    private val e999 = TestMethod("d", 999.0)
    private val rawData = mutableListOf(a1, b2, c3, d4)

    @Test
    fun fillShard_1234() {
        val testMethods = mutableListOf<TestMethod>().apply { addAll(rawData) }
        val startSize = testMethods.size
        var index = 1

        while (testMethods.iterator().hasNext()) {
            val testMethod = testMethods.iterator().next()

            assertThat(Shard.build(testMethods, testMethod.time).testMethods).isEqualTo(listOf(testMethod))
            assertThat(testMethods.size).isEqualTo(startSize - index)

            index += 1
        }

        assertThat(testMethods).isEmpty()
    }

    @Test
    fun fillShard_10() {
        val testMethods = mutableListOf<TestMethod>().apply { addAll(rawData) }

        val totalTime = testMethods.sumByDouble { it.time }
        val actual = Shard.build(testMethods, totalTime).testMethods
        assertThat(actual).isEqualTo(listOf(a1, b2, c3, d4))
    }

    @Test
    fun fillShard_999() {
        val actual = Shard.build(mutableListOf(e999), 0.0).testMethods
        assertThat(actual).isEqualTo(listOf(e999))
    }
}
