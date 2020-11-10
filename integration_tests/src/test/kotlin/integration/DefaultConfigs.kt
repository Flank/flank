package integration

val androidRunCommands = listOf("firebase", "test", "android", "run")
val iosRunCommands = listOf("firebase", "test", "ios", "run")
private const val EMPTY_STRING = ""
private const val NULL_STRING = "null"

val defaultCommonConfig = mapOf<String, Any>(
    "results-bucket" to "test-lab-[a-zA-Z0-9-]*",
    "results-dir" to "[.a-zA-Z0-9_-]*",
    "record-video" to false,
    "timeout" to "15m",
    "async" to false,
    "client-details" to EMPTY_STRING,
    "network-profile" to NULL_STRING,
    "results-history-name" to NULL_STRING,
    "num-flaky-test-attempts" to 0,
    "directories-to-pull" to EMPTY_STRING,
    "other-files" to EMPTY_STRING,
    "max-test-shards" to 1,
    "shard-time" to -1,
    "num-test-runs" to 1,
    "smart-flank-gcs-path" to EMPTY_STRING,
    "smart-flank-disable-upload" to false,
    "default-test-time" to 120.0,
    "use-average-test-time-for-new-tests" to false,
    "files-to-download" to EMPTY_STRING,
    "test-targets-always-run" to EMPTY_STRING,
    "disable-sharding" to false,
    "project" to "flank-open-source",
    "local-result-dir" to "results",
    "full-junit-result" to false,
    "keep-file-path" to false,
    "ignore-failed-tests" to false,
    "output-style" to "verbose",
    "disable-results-upload" to false,
    "default-class-test-time" to 240.0,
    "run-timeout" to -1,
    "scenario-numbers" to EMPTY_STRING,
)

val defaultAndroidConfig = defaultCommonConfig + mapOf<String, Any>(
    "app" to "[0-9a-zA-Z\\/_]*.apk",
    "test" to NULL_STRING,
    "additional-apks" to EMPTY_STRING,
    "auto-google-login" to false,
    "use-orchestrator" to true,
    "grant-permissions" to "all",
    "type" to NULL_STRING,
    "scenario-labels" to EMPTY_STRING,
    "obb-files" to EMPTY_STRING,
    "obb-names" to EMPTY_STRING,
    "performance-metrics" to false,
    "num-uniform-shards" to NULL_STRING,
    "test-runner-class" to NULL_STRING,
    "test-targets" to EMPTY_STRING,
    "robo-directives" to EMPTY_STRING,
    "robo-script" to NULL_STRING,
    "device" to """
    - model: NexusLowRes
      version: 28
      locale: en
      orientation: portrait
""".replace(System.lineSeparator(), "[\\s]*"),
    "additional-app-test-apks" to EMPTY_STRING,
    "legacy-junit-result" to false,
)

val defaultIosConfig = mapOf<String, Any>(
    "test" to "[0-9a-zA-Z\\/_]*.zip",
    "xctestrun-file" to "[0-9a-zA-Z\\/_].xctestrun",
    "xcode-version" to NULL_STRING,
    "device" to """
    - model: iphone8
      version: 12.0
      locale: en
      orientation: portrait
""".replace(System.lineSeparator(), "[\\s]*"),
    "additional-ipas" to EMPTY_STRING,
    "type" to "xctest",
    "app" to EMPTY_STRING,
    "test-special-entitlements" to false
)
