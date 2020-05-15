package ftl.args

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.google.api.client.json.GenericJson
import com.google.api.client.json.JsonObjectParser
import com.google.api.client.util.Charsets
import com.google.cloud.ServiceOptions
import com.google.cloud.storage.BucketInfo
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageClass
import com.google.cloud.storage.StorageOptions
import ftl.args.yml.IYmlMap
import ftl.args.yml.YamlObjectMapper
import ftl.config.FtlConstants
import ftl.config.FtlConstants.GCS_PREFIX
import ftl.config.FtlConstants.JSON_FACTORY
import ftl.config.FtlConstants.defaultCredentialPath
import ftl.config.FtlConstants.useMock
import ftl.gc.GcStorage
import ftl.gc.GcToolResults
import ftl.reports.xml.model.JUnitTestResult
import ftl.shard.Shard
import ftl.shard.StringShards
import ftl.shard.stringShards
import ftl.util.FlankFatalError
import ftl.util.FlankTestMethod
import ftl.util.assertNotEmpty
import java.io.File
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.regex.Pattern

object ArgsHelper {

    val yamlMapper: ObjectMapper by lazy {
        YamlObjectMapper().registerKotlinModule()
    }

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
            throw FlankFatalError("'$file' $name doesn't exist")
        }
    }

    fun assertCommonProps(args: IArgs) {
        assertNotEmpty(
            args.project, "The project is not set. Define GOOGLE_CLOUD_PROJECT, set project in flank.yml\n" +
                    "or save service account credential to ${defaultCredentialPath}\n" +
                    " See https://github.com/GoogleCloudPlatform/google-cloud-java#specifying-a-project-id"
        )

        if (args.maxTestShards !in IArgs.AVAILABLE_SHARD_COUNT_RANGE && args.maxTestShards != -1)
            throw FlankFatalError("max-test-shards must be >= 1 and <= 50, or -1. But current is ${args.maxTestShards}")

        if (args.shardTime <= 0 && args.shardTime != -1) throw FlankFatalError("shard-time must be >= 1 or -1")
        if (args.repeatTests < 1) throw FlankFatalError("num-test-runs must be >= 1")

        if (args.smartFlankGcsPath.isNotEmpty()) {
            if (!args.smartFlankGcsPath.startsWith(GCS_PREFIX)) {
                throw FlankFatalError("smart-flank-gcs-path must start with gs://")
            }
            if (args.smartFlankGcsPath.count { it == '/' } <= 2 || !args.smartFlankGcsPath.endsWith(".xml")) {
                throw FlankFatalError("smart-flank-gcs-path must be in the format gs://bucket/foo.xml")
            }
        }
    }

    fun evaluateFilePath(filePath: String): String {
        var file = filePath.trim().replaceFirst(Regex("^~"), System.getProperty("user.home"))
        file = evaluateEnvVars(file)
        // avoid File(..).canonicalPath since that will resolve symlinks
        file = Paths.get(file).toAbsolutePath().normalize().toString()

        // Avoid walking the folder's parent dir if we know it exists already.
        if (File(file).exists()) return file

        val filePaths = walkFileTree(file)
        if (filePaths.size > 1) {
            throw FlankFatalError("'$file' ($filePath) matches multiple files: $filePaths")
        } else if (filePaths.isEmpty()) {
            throw FlankFatalError("'$file' not found ($filePath)")
        }

        return filePaths.first().toAbsolutePath().normalize().toString()
    }

    fun assertGcsFileExists(uri: String) {
        if (!uri.startsWith(GCS_PREFIX)) {
            throw IllegalArgumentException("must start with $GCS_PREFIX uri: $uri")
        }

        val gcsURI = URI.create(uri)
        val bucket = gcsURI.authority
        val path = gcsURI.path.drop(1) // Drop leading slash

        val blob = GcStorage.storage.get(bucket, path)

        if (blob == null) {
            throw FlankFatalError("The file at '$uri' does not exist")
        }
    }

    fun validateTestMethods(
        testTargets: List<String>,
        validTestMethods: Collection<String>,
        from: String,
        skipValidation: Boolean = useMock
    ) {
        val missingMethods = testTargets - validTestMethods

        if (!skipValidation && missingMethods.isNotEmpty()) throw FlankFatalError("$from is missing methods: $missingMethods.\nValid methods:\n$validTestMethods")
        if (validTestMethods.isEmpty()) throw FlankFatalError("$from has no tests")
    }

    fun createJunitBucket(projectId: String, junitGcsPath: String) {
        if (useMock || junitGcsPath.isEmpty()) return
        val bucket = junitGcsPath.drop(GCS_PREFIX.length).substringBefore('/')
        createGcsBucket(projectId, bucket)
    }

    // Make best effort to list/create the bucket.
    // Due to permission issues, the user may not be able to list or create buckets.
    fun createGcsBucket(projectId: String, bucket: String): String {
        if (bucket.isEmpty()) return GcToolResults.getDefaultBucket(projectId)
            ?: throw RuntimeException("Failed to make bucket for $projectId")
        if (useMock) return bucket

        // test lab supports using a special free storage bucket
        // because we don't have access to the root account, it won't show up in the storage list.
        if (bucket.startsWith("test-lab-")) return bucket

        val storage = StorageOptions.newBuilder()
            .setCredentials(FtlConstants.credential)
            .setProjectId(projectId)
            .build().service
        val bucketLabel = mapOf("flank" to "")
        val storageLocation = "us-central1"

        val bucketListOption = Storage.BucketListOption.prefix(bucket)
        val storageList = emptyList<String>()

        try {
            storage.list(bucketListOption).values?.map { it.name }
        } catch (e: Exception) {
            // User may not have list permission
        }

        if (storageList.contains(bucket)) return bucket

        try {
            storage.create(
                BucketInfo.newBuilder(bucket)
                    .setStorageClass(StorageClass.REGIONAL)
                    .setLocation(storageLocation)
                    .setLabels(bucketLabel)
                    .build()
            )
        } catch (e: Exception) {
            // User may not have create permission
        }

        return bucket
    }

    private fun serviceAccountProjectId(): String? {
        try {
            if (!defaultCredentialPath.toFile().exists()) return null

            return JsonObjectParser(JSON_FACTORY).parseAndClose(
                Files.newInputStream(defaultCredentialPath),
                Charsets.UTF_8,
                GenericJson::class.java
            )["project_id"] as String
        } catch (e: Exception) {
            println("Parsing $defaultCredentialPath failed:")
            println(e.printStackTrace())
        }

        return null
    }

    fun getDefaultProjectId(): String? {
        if (useMock) return "mockProjectId"

        // Allow users control over project by checking using Google's logic first before falling back to JSON.
        return ServiceOptions.getDefaultProjectId() ?: serviceAccountProjectId()
    }

    // https://stackoverflow.com/a/2821201/2450315
    private val envRegex = Pattern.compile("\\$([a-zA-Z_]+[a-zA-Z0-9_]*)")

    private fun evaluateEnvVars(text: String): String {
        val buffer = StringBuffer()
        val matcher = envRegex.matcher(text)
        while (matcher.find()) {
            val envName = matcher.group(1)
            val envValue: String? = System.getenv(envName)
            if (envValue == null) {
                println("WARNING: $envName not found")
            }
            matcher.appendReplacement(buffer, envValue ?: "")
        }
        matcher.appendTail(buffer)
        return buffer.toString()
    }

    private fun walkFileTree(filePath: String): List<Path> {
        val searchDir = Paths.get(filePath).parent

        return ArgsFileVisitor("glob:$filePath").walk(searchDir)
    }

    fun calculateShards(filteredTests: List<FlankTestMethod>, args: IArgs): ShardChunks {
        if (filteredTests.isEmpty()) {
            // Avoid unnecessary computing if we already know there aren't tests to run.
            return listOf(emptyList())
        }
        val shards = if (args.disableSharding) {
            // Avoid to cast it to MutableList<String> to be agnostic from the caller.
            listOf(filteredTests.map { it.testName }.toMutableList())
        } else {
            val oldTestResult = GcStorage.downloadJunitXml(args) ?: JUnitTestResult(mutableListOf())
            val shardCount = Shard.shardCountByTime(filteredTests, oldTestResult, args)
            Shard.createShardsByShardCount(filteredTests, oldTestResult, args, shardCount).stringShards()
        }

        return testMethodsAlwaysRun(shards, args)
    }

    private fun testMethodsAlwaysRun(shards: StringShards, args: IArgs): StringShards {
        val alwaysRun = args.testTargetsAlwaysRun

        shards.forEach { shard ->
            shard.removeAll(alwaysRun)
            shard.addAll(0, alwaysRun)
        }

        return shards
    }
}
