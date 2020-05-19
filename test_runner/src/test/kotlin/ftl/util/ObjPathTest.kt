package ftl.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ObjPathTest {

    @Test
    fun parse() {
        val path = "2019-03-22_15-39-20.400000_ESdl/matrix_1/NexusLowRes-28-en-portrait-shard_0/com/package/name/b.txt"
        val parsed = ObjPath.parse(path)

        assertThat(parsed.objName).isEqualTo("2019-03-22_15-39-20.400000_ESdl")
        assertThat(parsed.fileName).isEqualTo("b.txt")
        assertThat(parsed.shardName).isEqualTo("shard_0")
        assertThat(parsed.deviceName).isEqualTo("NexusLowRes-28-en-portrait")
        assertThat(parsed.filePathName).isEqualTo("com/package/name")
        assertThat(parsed.matrixName).isEqualTo("matrix_1")
    }

    @Test
    fun legacyParse() {
        val path = "2019-03-22_15-39-20.400000_ESdl/shard_0/NexusLowRes-28-en-portrait/com/package/name/b.txt"
        val parsed = ObjPath.legacyParse(path)

        assertThat(parsed.objName).isEqualTo("2019-03-22_15-39-20.400000_ESdl")
        assertThat(parsed.fileName).isEqualTo("b.txt")
        assertThat(parsed.shardName).isEqualTo("shard_0")
        assertThat(parsed.deviceName).isEqualTo("NexusLowRes-28-en-portrait")
        assertThat(parsed.filePathName).isEqualTo("com/package/name")
    }
}
