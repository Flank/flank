package flank.tool.analytics.mixpanel

import flank.tool.analytics.mixpanel.internal.addToReport
import flank.tool.analytics.mixpanel.internal.configureReport
import flank.tool.analytics.mixpanel.internal.filterSensitiveValues
import flank.tool.analytics.mixpanel.internal.objectToMap
import flank.tool.analytics.mixpanel.internal.removeNotNeededKeys
import flank.tool.analytics.mixpanel.internal.initStatisticsClient
import flank.tool.analytics.mixpanel.internal.sendReport
import java.util.UUID
import kotlin.reflect.KClass

object Mixpanel {
    const val SESSION_ID = "session.id"
    const val PROJECT_ID = "project_id"
    const val PROJECT_NAME = "name"
    const val SCHEMA_VERSION = "schema_version"
    const val FLANK_VERSION = "flank_version"
    const val TEST_PLATFORM = "test_platform"
    const val CONFIGURATION = "configuration"
    const val DEVICE_TYPES = "device_types"
    const val APP_ID = "app_id"

    object Platform {
        const val FIREBASE = "firebase"
    }

    const val schemaVersion = "1.0"

    val sessionId by lazy { UUID.randomUUID().toString() }

    fun configure(projectName: String): Unit = configureReport(projectName)

    fun add(key: String, reportNode: Any): Unit = addToReport(key, reportNode)

    fun send(eventName: String): Unit = sendReport(eventName)

    fun initializeStatisticsClient(blockUsageStatistics: Boolean, vararg statisticClasses: KClass<*>): Unit =
        initStatisticsClient(blockUsageStatistics, statisticClasses)
}

fun Any.objectToMap(): Map<String, Any> = objectToMap()

fun Map<String, Any>.filterSensitiveValues() = removeNotNeededKeys().filterSensitiveValues()
