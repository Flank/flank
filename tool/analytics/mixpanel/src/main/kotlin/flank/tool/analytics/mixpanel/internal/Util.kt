package flank.tool.analytics.mixpanel.internal

import com.fasterxml.jackson.core.type.TypeReference
import java.util.UUID

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

const val SESSION_ID = "session.id"

val sessionId by lazy {
    UUID.randomUUID().toString()
}

fun Any.objectToMap(): Map<String, Any> =
    objectMapper.convertValue(this, object : TypeReference<Map<String, Any>>() {})
