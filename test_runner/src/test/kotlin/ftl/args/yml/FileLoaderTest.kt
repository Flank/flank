package ftl.args.yml

import ftl.util.loadFile
import ftl.test.util.TestHelper.getThrowable
import ftl.util.FlankFatalError
import org.junit.Assert
import org.junit.Test
import java.nio.file.Paths
import java.util.UUID

class FileLoaderTest {
    @Test(expected = FlankFatalError::class)
    fun `should throws FlankFatalError when file not found`() {
        val filePath = Paths.get("${UUID.randomUUID()}.yml")
        loadFile(filePath)
    }

    @Test
    fun `should throws FlankFatalError with specific message when file not found`() {
        val filePath = Paths.get("${UUID.randomUUID()}.yml")
        val thrownException = getThrowable { loadFile(filePath) }

        val expectedExceptionMessage = "File not found: $filePath"
        Assert.assertEquals(expectedExceptionMessage, thrownException.message)

        val expectedExceptionType = FlankFatalError::class
        Assert.assertEquals(expectedExceptionType, thrownException::class)
    }
}
