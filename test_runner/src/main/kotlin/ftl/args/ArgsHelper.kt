package ftl.args

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.google.api.client.json.GenericJson
import com.google.api.client.json.JsonObjectParser
import com.google.cloud.ServiceOptions
import com.google.cloud.storage.BucketInfo
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageClass
import com.google.cloud.storage.StorageOptions
import flank.common.defaultCredentialPath
import flank.common.isWindows
import flank.common.logLn
import ftl.api.downloadAsJunitXML
import ftl.args.IArgs.Companion.AVAILABLE_PHYSICAL_SHARD_COUNT_RANGE
import ftl.args.yml.YamlObjectMapper
import ftl.client.google.GcStorage
import ftl.client.google.GcToolResults
import ftl.client.google.credential
import ftl.config.FtlConstants.GCS_PREFIX
import ftl.config.FtlConstants.JSON_FACTORY
import ftl.config.FtlConstants.useMock
import ftl.run.exception.FlankConfigurationError
import ftl.run.exception.FlankGeneralError
import ftl.shard.Chunk
import ftl.shard.TestMethod
import ftl.shard.createShardsByShardCount
import ftl.shard.shardCountByTime
import ftl.util.FlankTestMethod
import ftl.util.assertNotEmpty
import ftl.util.getGACPathOrEmpty
import ftl.util.getSmartFlankGCSPathAsFileReference
import java.io.File
import java.net.URI
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.InvalidPathException
import java.nio.file.Path
import java.nio.file.Paths
import java.util.regex.Pattern

object ArgsHelper {

    val yamlMapper: ObjectMapper by lazy {
        YamlObjectMapper().registerKotlinModule()
    }

    private var projectIdSource: String? = null

    fun assertFileExists(file: String, name: String) {
        if (!file.exist()) throw FlankGeneralError("'$file' $name doesn't exist")
    }

    private fun String.convertToWindowsPath() =
        this.replace("/", "\\").replaceFirst("~", System.getProperty("user.home"))

    private fun String.exist() =
        if (startsWith(GCS_PREFIX)) GcStorage.exist(this) else File(this).exists()

    fun assertCommonProps(args: IArgs) {
        assertNotEmpty(
            args.project,
            "The project is not set. Define GOOGLE_CLOUD_PROJECT, set project in flank.yml\n" +
                "or save service account credential to ${defaultCredentialPath}\n" +
                " See https://github.com/GoogleCloudPlatform/google-cloud-java#specifying-a-project-id"
        )

        if (args.maxTestShards !in AVAILABLE_PHYSICAL_SHARD_COUNT_RANGE && args.maxTestShards != -1)
            throw FlankConfigurationError("max-test-shards must be >= ${AVAILABLE_PHYSICAL_SHARD_COUNT_RANGE.first} and <= ${AVAILABLE_PHYSICAL_SHARD_COUNT_RANGE.last}. But current is ${args.maxTestShards}")

        if (args.shardTime <= 0 && args.shardTime != -1) throw FlankConfigurationError("shard-time must be >= 1 or -1")
        if (args.repeatTests < 1) throw FlankConfigurationError("num-test-runs must be >= 1")

        if (args.smartFlankGcsPath.isNotEmpty()) {
            if (!args.smartFlankGcsPath.startsWith(GCS_PREFIX)) {
                throw FlankConfigurationError("smart-flank-gcs-path must start with gs://")
            }
            if (args.smartFlankGcsPath.count { it == '/' } <= 2 || !args.smartFlankGcsPath.endsWith(".xml")) {
                throw FlankConfigurationError("smart-flank-gcs-path must be in the format gs://bucket/foo.xml")
            }
        }
    }

    private fun String.inferCorrectPath() = if (isWindows) this.trim().convertToWindowsPath()
    else this.trim().replaceFirst(Regex("^~"), System.getProperty("user.home"))

    fun evaluateFilePath(filePath: String): String {
        var file = filePath.inferCorrectPath()
        file = evaluateEnvVars(file)
        // avoid File(..).canonicalPath since that will resolve symlinks
        try {
            file = Paths.get(file).toAbsolutePath().normalize().toString()
        } catch (e: InvalidPathException) {
            throw FlankGeneralError("Invalid path exception: $e")
        }

        // Avoid walking the folder's parent dir if we know it exists already.
        if (File(file).exists()) return file

        val filePaths = walkFileTree(file)
        if (filePaths.size > 1) {
            throw FlankGeneralError("'$file' ($filePath) matches multiple files: $filePaths")
        } else if (filePaths.isEmpty()) {
            throw FlankGeneralError("'$file' not found ($filePath)")
        }

        return filePaths.first().toAbsolutePath().normalize().toString()
    }

    fun assertGcsFileExists(uri: String) {
        if (!uri.startsWith(GCS_PREFIX)) {
            throw FlankConfigurationError("must start with $GCS_PREFIX uri: $uri")
        }

        val gcsURI = URI.create(uri)
        val bucket = gcsURI.authority
        val path = gcsURI.path.drop(1) // Drop leading slash

        GcStorage.storage.get(bucket, path) ?: throw FlankGeneralError("The file at '$uri' does not exist")
    }

    fun validateTestMethods(
        testTargets: List<String>,
        validTestMethods: Collection<String>,
        from: String,
        skipValidation: Boolean = useMock
    ) {
        val missingMethods = testTargets - validTestMethods

        if (!skipValidation && missingMethods.isNotEmpty()) throw FlankConfigurationError("$from is missing methods: $missingMethods.\nValid methods:\n$validTestMethods")
        if (validTestMethods.isEmpty()) throw FlankConfigurationError("$from has no tests")
    }

    fun createJunitBucket(projectId: String, junitGcsPath: String) {
        if (useMock || junitGcsPath.isBlank()) return
        val bucket = junitGcsPath.drop(GCS_PREFIX.length).substringBefore('/')
        createGcsBucket(projectId, bucket)
    }

    // Make best effort to list/create the bucket.
    // Due to permission issues, the user may not be able to list or create buckets.
    fun createGcsBucket(projectId: String, bucket: String): String {
        if (bucket.isBlank()) return GcToolResults.getDefaultBucket(projectId, projectIdSource)
            ?: throw FlankGeneralError("Failed to make bucket for $projectId")
        if (useMock) return bucket

        // test lab supports using a special free storage bucket
        // because we don't have access to the root account, it won't show up in the storage list.
        if (bucket.startsWith("test-lab-")) return bucket

        val storage = StorageOptions.newBuilder()
            .setCredentials(credential)
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
            logLn("Warning: Failed to make bucket for $projectId\nCause: ${e.message}")
        }

        return bucket
    }

    fun getDefaultProjectIdOrNull() = (if (useMock) "mock-project-id" else getUserProjectId())?.toLowerCase()

    // Allow users control over project by checking using Google's logic first before falling back to JSON.
    private fun getUserProjectId(): String? = fromUserProvidedCredentials()
        ?: ServiceOptions.getDefaultProjectId()?.let { if (it.isBlank()) null else it }
        ?: fromDefaultCredentials()

    private fun fromDefaultCredentials() = getProjectIdFromJson(defaultCredentialPath)

    private fun fromUserProvidedCredentials() =
        getProjectIdFromJson(Paths.get(getGACPathOrEmpty()))

    private fun getProjectIdFromJson(jsonPath: Path): String? = if (!jsonPath.toFile().exists()) null
    else runCatching {
        projectIdSource = jsonPath.toAbsolutePath().toString()
        JsonObjectParser(JSON_FACTORY).parseAndClose(
            Files.newInputStream(jsonPath),
            StandardCharsets.UTF_8,
            GenericJson::class.java
        )["project_id"] as String
    }.onFailure {
        logLn("Parsing $jsonPath failed:")
        logLn(it.printStackTrace())
    }.getOrNull()

    // https://stackoverflow.com/a/2821201/2450315
    private val envRegex = Pattern.compile("\\$([a-zA-Z_]+[a-zA-Z0-9_]*)")

    private fun evaluateEnvVars(text: String): String {
        val buffer = StringBuffer()
        val matcher = envRegex.matcher(text)
        while (matcher.find()) {
            val envName = matcher.group(1)
            val envValue: String? = System.getenv(envName)
            if (envValue == null) {
                logLn("WARNING: $envName not found")
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

    fun calculateShards(
        filteredTests: List<FlankTestMethod>,
        args: IArgs,
        forcedShardCount: Int? = null
    ): CalculateShardsResult {
        if (filteredTests.isEmpty()) {
            return CalculateShardsResult(
                emptyList(),
                emptyList()
            ) // Avoid unnecessary computing if we already know there aren't tests to run.
        }
        val (ignoredTests, testsToExecute) = filteredTests.partition { it.ignored }
        val shards = if (args.disableSharding) {
            listOf(
                Chunk(
                    testsToExecute.map {
                        TestMethod(
                            name = it.testName,
                            isParameterized = it.isParameterizedClass,
                            time = 0.0
                        )
                    }
                )
            )
        } else {
            val oldTestResult = downloadAsJunitXML(args.getSmartFlankGCSPathAsFileReference())
            val shardCount = forcedShardCount ?: shardCountByTime(testsToExecute, oldTestResult, args)
            createShardsByShardCount(testsToExecute, oldTestResult, args, shardCount).map { Chunk(it.testMethods) }
        }

        return CalculateShardsResult(
            testMethodsAlwaysRun(shards, args),
            ignoredTestCases = ignoredTests.map { it.testName }
        )
    }

    fun testMethodsAlwaysRun(shards: List<Chunk>, args: IArgs): List<Chunk> {
        val alwaysRun = args.testTargetsAlwaysRun
        val find = shards.flatMap { it.testMethods }.filter { alwaysRun.contains(it.name) }

        return shards.map { Chunk(find + it.testMethods.filterNot { method -> find.contains(method) }) }
    }
}

fun String.normalizeFilePath(): String =
    if (startsWith(GCS_PREFIX)) this
    else try {
        ArgsHelper.evaluateFilePath(this)
    } catch (e: Throwable) {
        this
    }

fun List<String>.normalizeToTestTargets(): ShardChunks =
    if (isEmpty()) emptyList()
    else map { it.split(',', ';') }
