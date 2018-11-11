package ftl.test.util

import com.google.common.truth.Truth
import java.nio.file.Path
import java.nio.file.Paths

object TestHelper {

    fun assert(actual: Any, expected: Any) =
        Truth.assertThat(actual).isEqualTo(expected)

    fun getPath(path: String): Path =
        Paths.get(path).toAbsolutePath().normalize()

    fun getString(path: String): String =
        getPath(path).toString()

    fun String.absolutePath(): String = Paths.get(this).toAbsolutePath().normalize().toString()
}
