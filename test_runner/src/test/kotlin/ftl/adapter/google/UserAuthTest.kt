package ftl.adapter.google

import com.google.common.truth.Truth.assertThat
import ftl.adapter.google.UserAuth.Companion.userToken
import ftl.run.exception.FlankGeneralError
import ftl.test.util.TestHelper.getThrowable
import io.mockk.every
import io.mockk.mockkObject
import org.junit.Test
import java.io.File

class UserAuthTest {

    @Test
    fun `should delete file and throw exception with correct message for incorrect file`() {
        val tempFile = File.createTempFile("test_", ".Token")
        val expectedMessage = """
Could not load user authentication, please
 - login again using command: flank auth login
 - or try again to use The Application Default Credentials variable to login
        """.trimIndent()
        mockkObject(UserAuth.Companion) {
            every { userToken } returns tempFile.toPath()

            getThrowable { UserAuth.load() }.run {
                assertThat(this).isInstanceOf(FlankGeneralError::class.java)
                assertThat(message).isEqualTo(expectedMessage)
                assertThat(tempFile.exists()).isFalse()
            }
        }
    }
}
