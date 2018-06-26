package ftl

import com.google.api.services.testing.model.*
import com.google.api.services.toolresults.model.*
import com.google.gson.GsonBuilder
import com.google.gson.LongSerializationPolicy
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
import org.apache.log4j.BasicConfigurator
import java.util.concurrent.atomic.AtomicInteger

object Main {

    private val matrixIdCounter: AtomicInteger = AtomicInteger(0)

    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
        BasicConfigurator.configure();

        val server = embeddedServer(Netty, 8080) {
            install(ContentNegotiation) {
                // Fix: IllegalArgumentException: number type formatted as a JSON number cannot use @JsonString annotation
                val gson = GsonBuilder().setLongSerializationPolicy(LongSerializationPolicy.STRING).create()
                register(ContentType.Application.Json, GsonConverter(gson))
            }
            routing {
                get("/v1/testEnvironmentCatalog/android") {
                    println("Responding to GET ${call.request.uri}")

                    val versions = (18..28).map { v ->
                        var version = AndroidVersion()
                        version.id = v.toString()
                        version.apiLevel = v
                        version
                    }

                    val nexusLowRes = AndroidModel()
                    nexusLowRes.id = "NexusLowRes"
                    nexusLowRes.form = "VIRTUAL"
                    nexusLowRes.supportedVersionIds = listOf("23", "24", "25", "26", "27", "28")

                    val shamu = AndroidModel()
                    shamu.id = "shamu"
                    shamu.form = "PHYSICAL"
                    shamu.supportedVersionIds = listOf("21", "22", "23")

                    val androidCatalog = AndroidDeviceCatalog()
                            .setVersions(versions)
                            .setModels(listOf(nexusLowRes, shamu))

                    val catalog = TestEnvironmentCatalog()
                    catalog.androidDeviceCatalog = androidCatalog

                    call.respond(catalog)
                }

                get("/v1/testEnvironmentCatalog/ios") {
                    println("Responding to GET ${call.request.uri}")

                    val version = IosVersion()
                    version.id = "11.2"
                    version.majorVersion = 11
                    version.minorVersion = 2

                    val iphone8 = IosModel()
                    iphone8.id = "iphone8"
                    iphone8.name = "iPhone 8"
                    iphone8.supportedVersionIds = listOf("11.2")

                    val iphonex = IosModel()
                    iphonex.id = "iphonex"
                    iphonex.name = "iPhone X"
                    iphonex.supportedVersionIds = listOf("11.2")

                    val iosCatalog = IosDeviceCatalog()
                            .setVersions(listOf(version))
                            .setModels(listOf(iphone8, iphonex))

                    val catalog = TestEnvironmentCatalog()
                    catalog.iosDeviceCatalog = iosCatalog

                    call.respond(catalog)
                }

                get("/v1/projects/{projectId}/testMatrices/{matrixIdCounter}") {
                    println("Responding to GET ${call.request.uri}")
                    val projectId = call.parameters["projectId"]
                    val matrixId = call.parameters["matrixIdCounter"]

                    val testMatrix = TestMatrix()
                            .setProjectId(projectId)
                            .setTestMatrixId(matrixId)
                            .setState("FINISHED")

                    call.respond(testMatrix)
                }

                // GcTestMatrix.build
                // http://localhost:8080/v1/projects/delta-essence-114723/testMatrices
                post("/v1/projects/{projectId}/testMatrices") {
                    println("Responding to POST ${call.request.uri}")
                    val projectId = call.parameters["projectId"]

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

                // GcToolResults.getResults(toolResultsStep)
                // GET /toolresults/v1beta3/projects/delta-essence-114723/histories/1/executions/1/steps/1
                get("/toolresults/v1beta3/projects/{projectId}/histories/{historyId}/executions/{executionId}/steps/{stepId}") {
                    println("Responding to GET ${call.request.uri}")
                    // val projectId = call.parameters["projectId"]

                    val oneSecond = Duration().setSeconds(1)

                    val testTiming = TestTiming()
                            .setTestProcessDuration(oneSecond)

                    val testExecutionStep = TestExecutionStep()
                            .setTestTiming(testTiming)

                    val outcome = Outcome().setSummary("success")

                    val step = Step()
                            .setTestExecutionStep(testExecutionStep)
                            .setRunDuration(oneSecond)
                            .setOutcome(outcome)

                    call.respond(step)
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
        server.start(wait = true)
    }
}
