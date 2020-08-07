package flank.scripts.release.updatebugsnag

import com.github.kittinunf.result.Result
import com.github.kittinunf.result.failure
import com.google.common.truth.Truth.assertThat
import flank.scripts.FuelTestRunner
import flank.scripts.exceptions.BugsnagException
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(FuelTestRunner::class)
class UpdateBugSnagTest {

    @Test
    fun `Should return success with correct request`() {
        runBlocking {
            // given
            val expectedStatus = "success"

            // when
            val actual = updateBugsnag("success", "version", "url")

            // then
            assertThat(actual).isInstanceOf(Result.Success::class.java)
            assertThat(actual.get().status).isEqualTo(expectedStatus)
            assertThat(actual.get().errors).isEmpty()
        }
    }

    @Test
    fun `Should return failure when bad request`() {
        runBlocking {
            // given
            val expectedStatus = "failure"

            // when
            val actual = updateBugsnag("failure", "version", "url")

            // then
            assertThat(actual).isInstanceOf(Result.Failure::class.java)
            actual.failure {
                assertThat(it).isInstanceOf(BugsnagException::class.java)
                with((it as BugsnagException).body) {
                    assertThat(status).isEqualTo(expectedStatus)
                    assertThat(errors).isNotEmpty()
                }
            }
        }
    }
}
