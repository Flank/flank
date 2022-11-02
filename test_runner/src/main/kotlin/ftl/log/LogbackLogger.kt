package ftl.log

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import org.slf4j.LoggerFactory.getLogger
import kotlin.properties.Delegates

sealed class LogbackLogger(private val logger: Logger) : FlankLogger {

    constructor(logger: Any) : this(logger as Logger)

    final override var isEnabled: Boolean by Delegates.observable(false) { _, _, enable ->
        logger.level = if (enable)
            Level.ALL else
            Level.OFF
    }

    object Root : LogbackLogger(getLogger(Logger.ROOT_LOGGER_NAME))
}
