package ftl.log

import com.google.api.client.http.GoogleApiLogger

private val LOGGERS = listOf(
    LogbackLogger.Root,
    LogbackLogger.FlankBugsnag,
    GoogleApiLogger
)

fun setDebugLogging(enable: Boolean) = LOGGERS.forEach { logger ->
    logger.isEnabled = enable
}
