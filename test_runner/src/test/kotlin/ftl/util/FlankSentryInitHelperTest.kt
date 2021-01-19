package ftl.util

import ftl.log.LogbackLogger
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.util.UUID

private const val GSUTIL_FOLDER = ".gsutil"
private const val ANALYTICS_FILE = "analytics-uuid"
private const val DISABLED = "DISABLED\n"

class FlankSentryInitHelperTest {

    @get:Rule
    val folder = TemporaryFolder()

    @Before
    fun setUp() {
        LogbackLogger.FlankSentry.isEnabled = false
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `should not create Sentry object if user has analytics disabled`() {
        val subfolder = folder.newFolder(GSUTIL_FOLDER)
        File(subfolder, ANALYTICS_FILE).also { it.writeText(DISABLED) }

        assertNull(initCrashReporter(disabledCrashReporter = false, folder.root.absolutePath))
    }
    @Test
    fun `should not create Sentry object if user provided analytics-uuid`() {
        val subfolder = folder.newFolder(GSUTIL_FOLDER)
        File(subfolder, ANALYTICS_FILE).also { it.writeText(UUID.randomUUID().toString()) }

        assertNotNull(initCrashReporter(disabledCrashReporter = false, folder.root.absolutePath))
    }

    @Test
    fun `should create Sentry object if mock server used`() {
        val subfolder = folder.newFolder(GSUTIL_FOLDER)
        File(subfolder, ANALYTICS_FILE).also { it.writeText(UUID.randomUUID().toString()) }

        assertNull(initCrashReporter(disabledCrashReporter = true, folder.root.absolutePath))
    }

    @Test
    fun `should not create Sentry object when running tests`() {
        assertNull(initCrashReporter())
    }
}
