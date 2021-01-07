package ftl.util

import ftl.config.FtlConstants
import io.sentry.Sentry
import java.io.File
import java.util.UUID

private const val FLANK_API_KEY = "https://f626934e7811480db91c40f62f5035f0@o475862.ingest.sentry.io/5514444"
private const val GSUTIL_FOLDER = ".gsutil"
private const val ANALYTICS_FILE = "analytics-uuid"
private const val DISABLED = "DISABLED"

enum class CrashReportTag(val tagName: String) {
    SESSION_ID("session.id"),
    OS_NAME("os.name"),
    FLANK_VERSION("flank.version"),
    FLANK_REVISION("flank.revision"),
    DEVICE_SYSTEM("device.system"),
    TEST_TYPE("test.type")
}

val captureError by lazy {
    initCrashReporter(FtlConstants.useMock)
    ::notify
}

internal fun initCrashReporter(
    useMock: Boolean,
    rootPath: String = System.getProperty("user.home")
) = when {
    useMock -> null
    analyticsFileExistAndIsDisabled(rootPath) -> null
    else -> initializeCrashReportWrapper()
}

private fun analyticsFileExistAndIsDisabled(rootPath: String) =
    File(rootPath, "$GSUTIL_FOLDER/$ANALYTICS_FILE").run { exists() && readText().trim() == DISABLED }

private fun initializeCrashReportWrapper() {
    Sentry.init {
        it.dsn = FLANK_API_KEY
        it.release = readRevision()
    }
    setCrashReportTag(CrashReportTag.SESSION_ID.tagName, sessionId)
    setCrashReportTag(CrashReportTag.OS_NAME.tagName, System.getProperty("os.name"))
    setCrashReportTag(CrashReportTag.FLANK_VERSION.tagName, readVersion())
    setCrashReportTag(CrashReportTag.FLANK_REVISION.tagName, readRevision())
}

val sessionId by lazy {
    UUID.randomUUID().toString()
}

fun setCrashReportTag(key: String, value: String) {
    Sentry.setTag(key, value)
}

private fun notify(error: Throwable) {
    Sentry.captureException(error)
}
