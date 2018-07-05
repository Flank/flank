package ftl.config

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.cloud.storage.BucketInfo
import com.google.cloud.storage.StorageClass
import com.google.cloud.storage.StorageOptions
import ftl.gc.GcStorage
import ftl.util.Utils
import java.io.File
import java.net.URI

abstract class GCloudConfig(
        @field:JsonProperty("results-bucket")
        val rootGcsBucket: String,
        @field:JsonProperty("performance-metrics")
        val disablePerformanceMetrics: Boolean = true,
        @field:JsonProperty("record-video")
        val disableRecordVideo: Boolean = false,
        @field:JsonProperty("timeout")
        val testTimeout: String = "60m",
        @field:JsonProperty("async")
        val waitForResults: Boolean = true,
        @field:JsonProperty("test-targets")
        val testMethods: List<String> = listOf(),
        @field:JsonProperty("project")
        val projectId: String = YamlConfig.getDefaultProjectId(),
        @field:JsonProperty("device")
        val devices: List<Device> = listOf()
) {

    private val storage = StorageOptions.newBuilder().setProjectId(projectId).build().service
    private val bucketLabel: Map<String, String> = mapOf(Pair("flank", ""))
    private val storageLocation = "us-central1"

    // Initialized in Android/iOS config init block
    lateinit var validTestNames: Collection<String>

    fun getGcsBucket(): String {
        if (FtlConstants.useMock) return rootGcsBucket

        val bucket = storage.list().values?.find { it.name == rootGcsBucket }
        if (bucket != null) return bucket.name

        return storage.create(BucketInfo.newBuilder(rootGcsBucket)
                .setStorageClass(StorageClass.REGIONAL)
                .setLocation(storageLocation)
                .setLabels(bucketLabel)
                .build()).name
    }

    fun validateTestMethods(validTestMethods: Collection<String>, from: String) {
        val missingMethods = testMethods - validTestMethods

        // todo: update YamConfigTest to use fixture apk with 155 tests, then remove useMock here.
        if (!FtlConstants.useMock && missingMethods.isNotEmpty()) Utils.fatalError("$from is missing methods: $missingMethods.\nValid methods:\n$validTestMethods")
        if (validTestMethods.isEmpty()) Utils.fatalError("$from has no tests")
    }

    protected fun assertFileExists(file: String, name: String) {
        if (!File(file).exists()) {
            Utils.fatalError("'$file' $name doesn't exist")
        }
    }

    protected fun assertGcsFileExists(uri: String) {
        val gcsURI = URI.create(uri)
        val bucket = gcsURI.authority
        val path = gcsURI.path.drop(1) // Drop leading slash

        val blob = GcStorage.storage.get(bucket, path)

        if (blob == null) {
            Utils.fatalError("The file at '$uri' does not exist")
        }
    }

    override fun toString() =
            """GCloud Config
  project: '$projectId'
  rootGcsBucket: '$rootGcsBucket',
  disablePerformanceMetrics: $disablePerformanceMetrics,
  disableRecordVideo: $disableRecordVideo,
  testTimeout: $testTimeout,
  async: $waitForResults,
  testMethods: $testMethods,
  devices: $devices
            """

}

