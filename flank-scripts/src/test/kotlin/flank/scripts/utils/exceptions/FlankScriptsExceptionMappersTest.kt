package flank.scripts.utils.exceptions

import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.failure
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.Test

class FlankScriptsExceptionMappersTest {

    @Test
    fun `Should properly map client error`() {
        // given
        val fuelMockedError = mockk<FuelError> {
            every { response.statusCode } returns 404
        }
        val result: Result<Any, FuelError> = Result.error(fuelMockedError)
        val expectedException = Exception("test")

        // when
        val (_, actualException) = result.mapClientError { expectedException }

        // then
        assertThat(actualException).isEqualTo(expectedException)
    }

    @Test
    fun `Should not map error when it is different than client error`() {
        // given
        val fuelMockedError = mockk<FuelError> {
            every { response.statusCode } returns 500
        }
        val result: Result<Any, FuelError> = Result.error(fuelMockedError)
        val testException = Exception("test")

        // when
        val actual = result.mapClientError { testException }

        // then
        assertThat(actual).isEqualTo(result)
        actual.failure {
            assertThat(it).isNotEqualTo(testException)
        }
    }

    @Test
    fun `Should not map error when response is success`() {
        // given
        val result: Result<Any, FuelError> = Result.success("")
        val testException = Exception("test")

        // when
        val actual = result.mapClientError { testException }

        // then
        assertThat(actual).isEqualTo(result)
        actual.failure {
            assertThat(it).isNotEqualTo(testException)
        }
    }

    @Test
    fun `Should properly map github exception from json body`() {
        // given
        val mockedFuelError = """
            {
              "message": "Bad credentials",
              "documentation_url": "https://developer.github.com/v3"
            }
        """.trimIndent().toMockFuelError()
        val expectedMessage = "Bad credentials"
        val expectedUrl = "https://developer.github.com/v3"
        val expectedToString = "Error while doing GitHub request, because of $expectedMessage, more info at $expectedUrl"

        // when
        val gitHubException = mockedFuelError.toGithubException()

        // then
        assertThat(gitHubException.body.message).isEqualTo(expectedMessage)
        assertThat(gitHubException.body.documentationUrl).isEqualTo(expectedUrl)
        assertThat(gitHubException.toString()).isEqualTo(expectedToString)
    }

    private fun String.toMockFuelError() = mockk<FuelError> {
        every { response.body().asString(any()) } returns this@toMockFuelError
    }
}
