package ftl.args.yml

import ftl.util.loadFile
import ftl.test.util.TestHelper.getThrowable
import ftl.util.FlankCommonException
import org.junit.Assert
import org.junit.Test
import java.nio.file.Paths
import java.util.UUID

class FileLoaderTest {
    @Test(expected = FlankCommonException::class)
    fun `should throws FlankCommonException when file not found`() {
        val filePath = Paths.get("${UUID.randomUUID()}.yml")
        loadFile(filePath)
    }

    @Test
    fun `should throws FlankCommonException with specific message when file not found`() {
        val filePath = Paths.get("${UUID.randomUUID()}.yml")
        val thrownException = getThrowable { loadFile(filePath) }

        val expectedExceptionMessage = "File not found: $filePath"
        Assert.assertEquals(expectedExceptionMessage, thrownException.message)

        val expectedExceptionType = FlankCommonException::class
        Assert.assertEquals(expectedExceptionType, thrownException::class)
    }
}
