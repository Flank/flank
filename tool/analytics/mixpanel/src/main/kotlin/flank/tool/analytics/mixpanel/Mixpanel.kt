package flank.tool.analytics.mixpanel

import flank.tool.analytics.mixpanel.internal.add
import flank.tool.analytics.mixpanel.internal.analyticsReport
import flank.tool.analytics.mixpanel.internal.configure
import flank.tool.analytics.mixpanel.internal.objectToMap
import flank.tool.analytics.mixpanel.internal.send

object Mixpanel {
    const val FIREBASE_TEST_LAB_RUN = "firebase test lab run"
    const val APP_ID = "app_id"
    const val DEVICE_TYPE = "device_type"
    const val FLANK_VERSION = "flank_version"
    const val FLANK_VERSION_PROPERTY = "version"
    const val TEST_PLATFORM = "test_platform"

    const val FIREBASE = "firebase"
    const val CORELLIUM = "corellium"

    const val ANDROID = "android"
    const val IOS = "ios"

    const val SCHEMA_VERSION = "schema_version"
    const val PROJECT_ID = "project_id"

    const val DEVICE_TYPES = "device_types"
    const val CONFIGURATION = "configuration"
    const val SESSION_ID = "session.id"

    fun configure(projectName: String) {
        analyticsReport.configure(projectName)
    }

    fun add(key: String, reportNode: Any) {
        analyticsReport.add(key, reportNode)
    }

    fun send(eventName: String = FIREBASE_TEST_LAB_RUN) =
        analyticsReport.send(eventName)
}

internal fun Any.objectToMap(): Map<String, Any> = objectToMap()
