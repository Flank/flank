package ftl.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ObfuscationGsonTest {

    @Test
    fun `Should return gson with correct type adapters registered`() {
        // given
        val expectedTypeTokenForAndroid = ListOfStringTypeToken
        val expectedTypeTokenForIos = ListOfStringListTypeToken

        // when
        val actualGson = obfuscatePrettyPrinter

        // then
        assertThat(actualGson.getAdapter(expectedTypeTokenForAndroid)).isNotNull()
        assertThat(actualGson.getAdapter(expectedTypeTokenForIos)).isNotNull()
    }
}
