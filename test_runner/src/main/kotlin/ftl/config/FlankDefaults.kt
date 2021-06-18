package ftl.config

/**
 * Flank specific default settings.
 * Values should assure best (quickest) test performance.
 * Each of value can be overwritten by config *.yml file
 */
object FlankDefaults {
    const val DISABLE_VIDEO_RECORDING = false
    const val DISABLE_AUTO_LOGIN = false
    const val DISABLE_PERFORMANCE_METRICS = false
    const val GRANT_PERMISSIONS_ALL = "all"
    const val DEFAULT_PARAMETERIZED_TESTS = "default"
}
