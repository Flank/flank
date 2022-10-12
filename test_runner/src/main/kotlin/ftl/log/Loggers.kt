package ftl.log

import com.google.api.client.http.GoogleApiLogger

private val LOGGERS = listOf(
    LogbackLogger.Root,
    GoogleApiLogger
)

fun setDebugLogging(enable: Boolean) = LOGGERS.forEach { logger ->
    logger.isEnabled = enable
}
