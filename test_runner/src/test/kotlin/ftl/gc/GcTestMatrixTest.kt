package ftl.gc

import ftl.args.IArgs
import ftl.test.util.FlankTestRunner
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@RunWith(FlankTestRunner::class)
class GcTestMatrixTest {

    @Test
    fun refresh_succeeds() {
        val args = mock(IArgs::class.java)
        `when`(args.projectId).thenReturn("789")
        GcTestMatrix.refresh("123", args)
    }
}
