package ftl.args

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.google.cloud.ServiceOptions
import com.google.cloud.storage.BucketInfo
import com.google.cloud.storage.StorageClass
import com.google.cloud.storage.StorageOptions
import com.google.common.math.IntMath
import ftl.args.yml.IYmlMap
import ftl.config.FtlConstants
import ftl.gc.GcStorage
import ftl.util.Utils
import java.io.File
import java.math.RoundingMode
import java.net.URI

object ArgsHelper {

    val yamlMapper by lazy { ObjectMapper(YAMLFactory()).registerModule(KotlinModule()) }

    fun mergeYmlMaps(vararg ymlMaps: IYmlMap): Map<String, List<String>> {
        val result = mutableMapOf<String, List<String>>()
        ymlMaps.map { it.map }
                .forEach { map ->
                    map.forEach { (k, v) ->
                        result.merge(k, v) { a, b -> a + b }
                    }
                }
        return result
    }

    fun assertFileExists(file: String, name: String) {
        if (!File(file).exists()) {
            Utils.fatalError("'$file' $name doesn't exist")
        }
    }

    fun assertGcsFileExists(uri: String) {
        val gcsURI = URI.create(uri)
        val bucket = gcsURI.authority
        val path = gcsURI.path.drop(1) // Drop leading slash

        val blob = GcStorage.storage.get(bucket, path)

        if (blob == null) {
            Utils.fatalError("The file at '$uri' does not exist")
        }
    }

    fun validateTestMethods(testTargets: List<String>, validTestMethods: Collection<String>, from: String) {
        val missingMethods = testTargets - validTestMethods

        // todo: update YamConfigTest to use fixture apk with 155 tests, then remove useMock here.
        if (!FtlConstants.useMock && missingMethods.isNotEmpty()) Utils.fatalError("$from is missing methods: $missingMethods.\nValid methods:\n$validTestMethods")
        if (validTestMethods.isEmpty()) Utils.fatalError("$from has no tests")
    }

    fun calculateShards(testTargets: Collection<String>,
                        validTestNames: Collection<String>,
                        testMethodsAlwaysRun: Collection<String>,
                        testShards: Int): List<List<String>> {
        val testShardMethods = if (testTargets.isNotEmpty()) {
            testTargets
        } else {
            validTestNames
        }.distinct().toMutableList()
        testShardMethods.removeAll(testMethodsAlwaysRun)

        var chunkSize = IntMath.divide(testShardMethods.size, testShards, RoundingMode.UP)
        // 1 method / 40 shard = 0. chunked(0) throws an exception.
        // default to running all tests in a single chunk if method count is less than shard count.
        if (chunkSize < 1) chunkSize = testShardMethods.size

        val testShardChunks = testShardMethods.chunked(chunkSize).map { testMethodsAlwaysRun + it }

        // Ensure we don't create more VMs than requested. VM count per run should be <= testShards
        if (testShardChunks.size > testShards) {
            Utils.fatalError("Calculated chunks $testShardChunks is > requested $testShards testShards.")
        }
        if (testShardChunks.isEmpty()) Utils.fatalError("Failed to populate test shard chunks")

        return testShardChunks
    }

    fun getGcsBucket(projectId: String, resultsBucket: String): String {
        if (FtlConstants.useMock) return resultsBucket
        // test lab supports using a special free storage bucket
        // because we don't have access to the root account, it won't show up in the storage list.
        if (resultsBucket.startsWith("test-lab-")) return resultsBucket

        val storage = StorageOptions.newBuilder().setProjectId(projectId).build().service
        val bucketLabel = mapOf(Pair("flank", ""))
        val storageLocation = "us-central1"

        val storageList = storage.list().values?.map { it.name } ?: emptyList()
        val bucket = storageList.find { it == resultsBucket }
        if (bucket != null) return bucket

        return storage.create(BucketInfo.newBuilder(resultsBucket)
                .setStorageClass(StorageClass.REGIONAL)
                .setLocation(storageLocation)
                .setLabels(bucketLabel)
                .build()).name
    }

    fun getDefaultProjectId(): String {
        if (FtlConstants.useMock) return "mockProjectId"

        return ServiceOptions.getDefaultProjectId() ?: ""
    }
}
