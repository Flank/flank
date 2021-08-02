package ftl.util

import flank.common.config.isTest
import flank.tool.analytics.mixpanel.SESSION_ID
import flank.tool.analytics.mixpanel.sessionId
import io.sentry.Sentry
import io.sentry.SentryLevel
import java.io.File

private const val FLANK_API_KEY = "https://f626934e7811480db91c40f62f5035f0@o475862.ingest.sentry.io/5514444"
private const val GSUTIL_FOLDER = ".gsutil"
private const val ANALYTICS_FILE = "analytics-uuid"
private const val DISABLED = "DISABLED"

const val OS_NAME = "os.name"
const val FLANK_VERSION = "flank.version"
const val FLANK_REVISION = "flank.revision"
const val DEVICE_SYSTEM = "device.system"
const val TEST_TYPE = "test.type"
const val PROJECT_ID = "project.id"

private val configureCrashReporter by lazy { initCrashReporter() }

private var crashReportingEnabled = true

fun disableCrashReporting() {
    crashReportingEnabled = false
    configureCrashReporter
}

fun Throwable.report(logLevel: SentryLevel = SentryLevel.ERROR) {
    if (isTest().not()) {
        configureCrashReporter
        notify(this, logLevel)
    }
}

internal fun initCrashReporter(
    disabledCrashReporter: Boolean = isTest() || crashReportingEnabled.not(),
    rootPath: String = System.getProperty("user.home")
) = when {
    disabledCrashReporter -> null
    isGoogleAnalyticsDisabled(rootPath) -> null
    else -> initializeCrashReportWrapper()
}

internal fun isGoogleAnalyticsDisabled(rootPath: String) =
    File(rootPath, "$GSUTIL_FOLDER/$ANALYTICS_FILE").run { exists() && readText().trim() == DISABLED }

private fun initializeCrashReportWrapper() {
    Sentry.init {
        it.dsn = FLANK_API_KEY
        it.release = readRevision()
    }
    setCrashReportTag(
        SESSION_ID to sessionId,
        OS_NAME to System.getProperty("os.name"),
        FLANK_VERSION to readVersion(),
        FLANK_REVISION to readRevision()
    )
}

fun setCrashReportTag(
    vararg tags: Pair<String, String>
) = tags.forEach { (property, value) -> Sentry.setTag(property, value) }

private fun notify(error: Throwable, logLevel: SentryLevel) {
    Sentry.withScope {
        it.level = logLevel
        Sentry.captureException(error)
    }
}

fun closeCrashReporter() {
    Sentry.endSession()
    Sentry.close()
}
