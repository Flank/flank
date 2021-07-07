package flank.tool.analytics

import java.util.UUID

const val CONFIGURATION_KEY = "configuration"
const val APP_ID = "app_id"
const val DEVICE_TYPE = "device_type"
const val FLANK_VERSION = "flank_version"
const val FLANK_VERSION_PROPERTY = "version"
const val TEST_PLATFORM = "test_platform"
const val FIREBASE = "firebase"

const val SESSION_ID = "session.id"

val sessionId by lazy {
    UUID.randomUUID().toString()
}
