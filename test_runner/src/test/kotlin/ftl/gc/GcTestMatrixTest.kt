package ftl.gc

import ftl.args.IArgs
import ftl.test.util.FlankTestRunner
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@RunWith(FlankTestRunner::class)
class GcTestMatrixTest {

    @Test
    fun `refresh succeeds`() {
        val args = mock(IArgs::class.java)
        `when`(args.project).thenReturn("789")
        runBlocking { GcTestMatrix.refresh("123", args) }
    }
}
