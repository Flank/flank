package ftl.util

import com.google.common.truth.Truth.assertThat
import ftl.test.util.FlankTestRunner
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(FlankTestRunner::class)
class ObjPathTest {

    @Test
    fun parse() {
        val path = "2019-03-22_15-39-20.400000_ESdl/shard_0/a/b.txt"
        val parsed = ObjPath.parse(path)

        assertThat(parsed.objName).isEqualTo("2019-03-22_15-39-20.400000_ESdl")
        assertThat(parsed.fileName).isEqualTo("b.txt")
        assertThat(parsed.afterObjName).isEqualTo("shard_0/a/b.txt")
        assertThat(parsed.shardName).isEqualTo("shard_0")
    }
}
