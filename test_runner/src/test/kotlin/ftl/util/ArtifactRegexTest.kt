package ftl.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ArtifactRegexTest {

    @Test
    fun regexExists() {
        assertThat(ArtifactRegex.testResultRgx).isNotNull()
        assertThat(ArtifactRegex.screenshotRgx).isNotNull()
    }
}
