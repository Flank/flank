package flank.scripts.utils

import org.junit.Test

class FileKtTest {

    @Test
    fun createSymbolicLinkTest() {
        createSymbolicLink(
            link = "../test_runner/src/test/kotlin/ftl/fixtures/tmp",
            target = "../test_artifacts/master"
        )
    }
}
