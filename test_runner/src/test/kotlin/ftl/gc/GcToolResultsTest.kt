package ftl.gc

import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.HttpResponseException
import com.google.api.services.testing.model.ToolResultsHistory
import com.google.common.truth.Truth.assertThat
import ftl.args.AndroidArgs
import ftl.config.FtlConstants
import ftl.test.util.FlankTestRunner
import ftl.test.util.TestHelper.getThrowable
import ftl.run.exception.FlankGeneralError
import ftl.run.exception.PermissionDenied
import ftl.run.exception.ProjectNotFound
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(FlankTestRunner::class)
class GcToolResultsTest {

    private val projectId = "123"

    @After
    fun tearDown() = unmockkAll()

    @Test
    fun `createToolResultsHistory null succeeds`() {
        val args = mockk<AndroidArgs>()
        every { args.project } returns projectId
        every { args.resultsHistoryName } returns null

        val expected = ToolResultsHistory().setProjectId(projectId)

        assertThat(GcToolResults.createToolResultsHistory(args)).isEqualTo(expected)
    }

    @Test
    fun `createToolResultsHistory succeeds`() {
        val args = mockk<AndroidArgs>()
        every { args.project } returns projectId
        every { args.resultsHistoryName } returns "custom history"

        val expected = ToolResultsHistory()
            .setProjectId(projectId)
            .setHistoryId("mockId")

        assertThat(GcToolResults.createToolResultsHistory(args)).isEqualTo(expected)
    }

    @Test
    fun `getDefaultBucket on 403 error should throw exception with specific message`() {
        val expected = """
            Flank encountered a 403 error when running on project $projectId. Please verify this credential is authorized for the project and has the required permissions.
            Consider authentication with a Service Account https://github.com/Flank/flank#authenticate-with-a-service-account
            or with a Google account https://github.com/Flank/flank#authenticate-with-a-google-account
            
            Caused by: com.google.api.client.googleapis.json.GoogleJsonResponseException: 403 Forbidden
            {
              "code" : 403,
              "errors" : [ {
                "domain" : "global",
                "message" : "The caller does not have permission",
                "reason" : "forbidden"
              } ],
              "message" : "The caller does not have permission",
              "status" : "PERMISSION_DENIED"
            }
        """.trimIndent()
        mockkObject(GcToolResults) {
            every { GcToolResults.service.applicationName } returns projectId

            val exceptionBuilder = mockk<HttpResponseException.Builder>()
            every { exceptionBuilder.message } returns """
            403 Forbidden
            {
              "code" : 403,
              "errors" : [ {
                "domain" : "global",
                "message" : "The caller does not have permission",
                "reason" : "forbidden"
              } ],
              "message" : "The caller does not have permission",
              "status" : "PERMISSION_DENIED"
            }
            """.trimIndent()
            val mockJSonException = GoogleJsonResponseException(exceptionBuilder, null)
            every { GcToolResults.service.Projects().initializeSettings(projectId) } throws PermissionDenied(mockJSonException)
            val exception = getThrowable { GcToolResults.getDefaultBucket(projectId) }
            assertEquals(expected, exception.message)
        }
    }

    @Test(expected = FlankGeneralError::class)
    fun `getDefaultBucket on PermissionDenied error should throw FlankGeneralError`() {
        mockkObject(GcToolResults) {
            every { GcToolResults.service.applicationName } returns projectId
            every { GcToolResults.service.Projects().initializeSettings(projectId) } throws PermissionDenied(IOException())
            GcToolResults.getDefaultBucket(projectId)
        }
    }

    @Test(expected = FlankGeneralError::class)
    fun `getDefaultBucket on ProjectNotFound error should throw FlankGeneralError`() {
        mockkObject(GcToolResults) {
            every { GcToolResults.service.applicationName } returns projectId
            every { GcToolResults.service.Projects().initializeSettings(projectId) } throws ProjectNotFound(IOException())
            GcToolResults.getDefaultBucket(projectId)
        }
    }

    @Test
    fun `getDefaultBucket on 404 error should throw exception with specific message`() {
        val expected = """
            Flank was unable to find project $projectId. Please verify the project id.
            
            Caused by: com.google.api.client.googleapis.json.GoogleJsonResponseException: 404 Not Found
            {
              "code" : 404,
              "errors" : [ {
                "domain" : "global",
                "message" : "Project not found: $projectId",
                "reason" : "notFound"
              } ],
              "message" : "Project not found: $projectId",
              "status" : "NOT_FOUND"
            }
        """.trimIndent()
        mockkObject(GcToolResults) {
            every { GcToolResults.service.applicationName } returns FtlConstants.applicationName

            val exceptionBuilder = mockk<HttpResponseException.Builder>()
            every { exceptionBuilder.message } returns """
            404 Not Found
            {
              "code" : 404,
              "errors" : [ {
                "domain" : "global",
                "message" : "Project not found: $projectId",
                "reason" : "notFound"
              } ],
              "message" : "Project not found: $projectId",
              "status" : "NOT_FOUND"
            }
            """.trimIndent()
            val mockJSonException = GoogleJsonResponseException(exceptionBuilder, null)
            every { GcToolResults.service.Projects().initializeSettings(projectId) } throws ProjectNotFound(mockJSonException)
            val exception = getThrowable { GcToolResults.getDefaultBucket(projectId) }
            assertEquals(expected, exception.message)
        }
    }
}
