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
        val resultsBucket: String,
        @field:JsonProperty("performance-metrics")
        val performanceMetrics: Boolean = false,
        @field:JsonProperty("record-video")
        val recordVideo: Boolean = true,
        @field:JsonProperty("timeout")
        val testTimeout: String = "60m",
        @field:JsonProperty("async")
        val async: Boolean = false,
        @field:JsonProperty("test-targets")
        val testTargets: List<String> = listOf(),
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
        if (FtlConstants.useMock) return resultsBucket

        val bucket = storage.list().values?.find { it.name == resultsBucket }
        if (bucket != null) return bucket.name

        return storage.create(BucketInfo.newBuilder(resultsBucket)
                .setStorageClass(StorageClass.REGIONAL)
                .setLocation(storageLocation)
                .setLabels(bucketLabel)
                .build()).name
    }

    fun validateTestMethods(validTestMethods: Collection<String>, from: String) {
        val missingMethods = testTargets - validTestMethods

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
  rootGcsBucket: '$resultsBucket',
  performanceMetrics: $performanceMetrics,
  recordVideo: $recordVideo,
  testTimeout: $testTimeout,
  async: $async,
  testMethods: $testTargets,
  devices: $devices
            """

}

