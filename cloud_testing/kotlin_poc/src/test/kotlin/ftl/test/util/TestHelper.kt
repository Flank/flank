package ftl.test.util

import com.google.common.truth.Truth
import java.nio.file.Paths

object TestHelper {

    fun assert(actual: Any, expected: Any) =
            Truth.assertThat(actual).isEqualTo(expected)

    fun getPath(path: String): String =
            Paths.get(path).normalize().toAbsolutePath().toString()
}
