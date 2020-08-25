package ftl.args.yml

import ftl.test.util.TestHelper.getThrowable
import ftl.run.exception.FlankGeneralError
import ftl.util.loadFile
import org.junit.Assert
import org.junit.Test
import java.nio.file.Paths
import java.util.UUID

class FileLoaderTest {
    @Test(expected = FlankGeneralError::class)
    fun `should throws FlankGeneralError when file not found`() {
        val filePath = Paths.get("${UUID.randomUUID()}.yml")
        loadFile(filePath)
    }

    @Test
    fun `should throws FlankGeneralError with specific message when file not found`() {
        val filePath = Paths.get("${UUID.randomUUID()}.yml")
        val thrownException = getThrowable { loadFile(filePath) }

        val expectedExceptionMessage = "File not found: $filePath"
        Assert.assertEquals(expectedExceptionMessage, thrownException.message)

        val expectedExceptionType = FlankGeneralError::class
        Assert.assertEquals(expectedExceptionType, thrownException::class)
    }
}
