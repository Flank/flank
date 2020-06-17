package ftl.gc

import org.junit.Assert
import org.junit.Test

class GcApkFileNameGeneratorTest {
    private val apkName = "app-debug.apk"

    @Test
    fun `apk name should't changed if counter is not set`() {
        val actual = generateApkFileName(apkName)
        Assert.assertEquals(apkName, actual)
    }

    @Test
    fun `apk name should contains file name with counter value`() {
        val counterValue = 1
        val actual = generateApkFileName(apkName, counterValue)
        val expected = "app-debug_$counterValue.apk"
        Assert.assertEquals(expected, actual)
    }
}
