package ftl.gc

import ftl.args.IArgs
import ftl.client.google.GcTestMatrix
import ftl.test.util.FlankTestRunner
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(FlankTestRunner::class)
class GcTestMatrixTest {

    @After
    fun tearDown() = unmockkAll()

    @Test
    fun `refresh succeeds`() {
        val args = mockk<IArgs>()
        every { args.project } returns "789"

        runBlocking { GcTestMatrix.refresh("123", args.project) }
    }
}
