package ftl.mock

import com.google.api.services.testing.model.AndroidDevice
import com.google.api.services.testing.model.AndroidDeviceCatalog
import com.google.api.services.testing.model.Environment
import com.google.api.services.testing.model.GoogleCloudStorage
import com.google.api.services.testing.model.IosDeviceCatalog
import com.google.api.services.testing.model.ResultStorage
import com.google.api.services.testing.model.TestEnvironmentCatalog
import com.google.api.services.testing.model.TestExecution
import com.google.api.services.testing.model.TestMatrix
import com.google.api.services.testing.model.ToolResultsStep
import com.google.api.services.toolresults.model.Duration
import com.google.api.services.toolresults.model.FailureDetail
import com.google.api.services.toolresults.model.History
import com.google.api.services.toolresults.model.InconclusiveDetail
import com.google.api.services.toolresults.model.ListHistoriesResponse
import com.google.api.services.toolresults.model.Outcome
import com.google.api.services.toolresults.model.ProjectSettings
import com.google.api.services.toolresults.model.SkippedDetail
import com.google.api.services.toolresults.model.Step
import com.google.api.services.toolresults.model.TestExecutionStep
import com.google.api.services.toolresults.model.TestTiming
import com.google.gson.GsonBuilder
import com.google.gson.LongSerializationPolicy
import ftl.config.FtlConstants
import ftl.config.FtlConstants.JSON_FACTORY
import ftl.log.LogbackLogger
import ftl.util.Bash
import ftl.util.StepOutcome.failure
import ftl.util.StepOutcome.inconclusive
import ftl.util.StepOutcome.skipped
import ftl.util.StepOutcome.success
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.gson.GsonConverter
import io.ktor.http.ContentType
import io.ktor.request.uri
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import java.net.BindException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.atomic.AtomicInteger

object MockServer {

    // FIXME incrementing matrixIdCounter during test may cause non deterministic behavior
    private val matrixIdCounter: AtomicInteger = AtomicInteger(0)
    const val port = 8080
    private var isStarted: Boolean = false

    private inline fun <reified T> loadCatalog(fileName: String): T {
        val jsonPath = Paths.get("./src/test/kotlin/ftl/fixtures/$fileName")
        if (!Files.exists(jsonPath)) throw RuntimeException("Path doesn't exist: $fileName")
        return JSON_FACTORY.fromReader(Files.newBufferedReader(jsonPath), T::class.java)
    }

    private val androidCatalog by lazy { loadCatalog<AndroidDeviceCatalog>("android_catalog.json") }
    private val iosCatalog by lazy { loadCatalog<IosDeviceCatalog>("ios_catalog.json") }

    private fun fakeStep(stringId: String): Step {
        val oneSecond = Duration().setSeconds(1)

        val testTiming = TestTiming()
            .setTestProcessDuration(oneSecond)

        val testExecutionStep = TestExecutionStep()
            .setTestTiming(testTiming)

        val outcome = Outcome()
        when (stringId) {
            "-1" -> {
                outcome.summary = failure
                val failureDetail = FailureDetail()
                failureDetail.timedOut = true
                outcome.failureDetail = failureDetail
            }
            "-2" -> {
                outcome.summary = inconclusive
                val inconclusiveDetail = InconclusiveDetail()
                inconclusiveDetail.abortedByUser = true
                outcome.inconclusiveDetail = inconclusiveDetail
            }
            "-3" -> {
                outcome.summary = skipped
                val skippedDetail = SkippedDetail()
                skippedDetail.incompatibleAppVersion = true
                outcome.skippedDetail = skippedDetail
            }
            else -> outcome.summary = success
        }

        return Step()
            .setTestExecutionStep(testExecutionStep)
            .setRunDuration(oneSecond)
            .setOutcome(outcome)
    }

    private val application by lazy {
        embeddedServer(Netty, port) {
            install(ContentNegotiation) {
                // Fix: IllegalArgumentException: number type formatted as a JSON number cannot use @JsonString annotation
                val gson = GsonBuilder().setLongSerializationPolicy(LongSerializationPolicy.STRING).create()
                register(ContentType.Application.Json, GsonConverter(gson))
            }
            routing {
                get("/v1/testEnvironmentCatalog/android") {
                    println("Responding to GET ${call.request.uri}")

                    val catalog = TestEnvironmentCatalog()
                    catalog.androidDeviceCatalog = androidCatalog

                    call.respond(catalog)
                }

                get("/v1/testEnvironmentCatalog/ios") {
                    println("Responding to GET ${call.request.uri}")

                    val catalog = TestEnvironmentCatalog()
                    catalog.iosDeviceCatalog = iosCatalog

                    call.respond(catalog)
                }

                get("/v1/projects/{project}/testMatrices/{matrixIdCounter}") {
                    println("Responding to GET ${call.request.uri}")
                    val projectId = call.parameters["project"]
                    val matrixId = call.parameters["matrixIdCounter"]

                    val testMatrix = TestMatrix()
                        .setProjectId(projectId)
                        .setTestMatrixId(matrixId)
                        .setState("FINISHED")

                    call.respond(testMatrix)
                }

                // GcTestMatrix.build
                // http://localhost:8080/v1/projects/flank-open-source/testMatrices
                post("/v1/projects/{project}/testMatrices") {
                    println("Responding to POST ${call.request.uri}")
                    val projectId = call.parameters["project"]

                    val matrixId = matrixIdCounter.incrementAndGet().toString()

                    val resultStorage = ResultStorage()
                    resultStorage.googleCloudStorage = GoogleCloudStorage()
                    resultStorage.googleCloudStorage.gcsPath = matrixId

                    val toolResultsStep = ToolResultsStep()
                        .setProjectId(projectId)
                        .setHistoryId(matrixId)
                        .setExecutionId(matrixId)
                        .setStepId(matrixId)

                    val device = AndroidDevice()
                        .setAndroidModelId("NexusLowRes")

                    val environment = Environment()
                        .setAndroidDevice(device)

                    val testExecution = TestExecution()
                        .setToolResultsStep(toolResultsStep)
                        .setEnvironment(environment)

                    val matrix = TestMatrix()
                        .setProjectId(projectId)
                        .setTestMatrixId("matrix-$matrixId")
                        .setState("FINISHED")
                        .setResultStorage(resultStorage)
                        .setTestExecutions(listOf(testExecution))

                    call.respond(matrix)
                }

                // GcToolResults.getStepResult(toolResultsStep)
                // GET /toolresults/v1beta3/projects/flank-open-source/histories/1/executions/1/steps/1
                get("/toolresults/v1beta3/projects/{project}/histories/{historyId}/executions/{executionId}/steps/{stepId}") {
                    println("Responding to GET ${call.request.uri}")
                    val stepId = call.parameters["stepId"] ?: ""
                    call.respond(fakeStep(stepId))
                }

                // GcToolResults.getExecutionResult(toolResultsStep)
                // GET /toolresults/v1beta3/projects/flank-open-source/histories/1/executions/1
                get("/toolresults/v1beta3/projects/{project}/histories/{historyId}/executions/{executionId}") {
                    println("Responding to GET ${call.request.uri}")
                    val executionId = call.parameters["executionId"] ?: ""
                    call.respond(fakeStep(executionId))
                }

                // GcToolResults.getDefaultBucket(project)
                post("/toolresults/v1beta3/projects/{project}:initializeSettings") {
                    val settings = ProjectSettings()
                        .setDefaultBucket("mockBucket")
                    call.respond(settings)
                }

                // GcToolResults.listHistoriesByName
                get("/toolresults/v1beta3/projects/{project}/histories") {
                    val response = ListHistoriesResponse()
                        .setHistories(null)
                    call.respond(response)
                }

                // GcToolResults.createHistory
                post("/toolresults/v1beta3/projects/{project}/histories") {
                    call.respond(
                        History()
                            .setHistoryId("mockId")
                            .setDisplayName("mockDisplayName")
                            .setName("mockName")
                    )
                }

                // POST /upload/storage/v1/b/tmp_bucket_2/o?projection=full&uploadType=multipart
                // GCS Storage uses multipart uploading. Mocking the post response and returning a blob
                // isn't sufficient to mock the upload process.

                // TODO: does ktor have a catch all route?
                post("/{...}") {
                    println("Unknown POST " + call.request.uri)
                    call.respond("")
                }

                get("/{...}") {
                    println("Unknown GET " + call.request.uri)
                    call.respond("")
                }
            }
        }
    }

    fun start() {
        if (isStarted) return
        val loggingEnabled = LogbackLogger.Root.isEnabled
        // Disable mock server initialization logs
        LogbackLogger.Root.isEnabled = false
        val server = application
        try {
            server.start(wait = false)
        } catch (e: BindException) {
            val lsofOutput = Bash.execute("lsof -i :$port")
            val pid = lsofOutput.split("\n").last().split(Regex("\\s+"))[1]
            Bash.execute("kill -9 $pid")
            Thread.sleep(2000)
            server.start(wait = false)
        }
        LogbackLogger.Root.isEnabled = loggingEnabled
        isStarted = true
        FtlConstants.useMock = true
        TestArtifact.checkFixtures
    }
}
