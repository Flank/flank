@file:Suppress("unused")
package flank.corellium.client.logging

import io.ktor.client.features.logging.LogLevel

sealed class LoggingLevel(val level: LogLevel) {
    object All : LoggingLevel(LogLevel.ALL)
    object None : LoggingLevel(LogLevel.NONE)
    object Body : LoggingLevel(LogLevel.BODY)
    object Headers : LoggingLevel(LogLevel.HEADERS)
    object Info : LoggingLevel(LogLevel.INFO)
}
