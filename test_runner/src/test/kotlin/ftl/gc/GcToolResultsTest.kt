package ftl.gc

import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.HttpResponseException
import com.google.common.truth.Truth.assertThat
import com.google.api.services.testing.model.ToolResultsHistory
import com.google.api.services.testing.model.ToolResultsStep
import ftl.args.AndroidArgs
import ftl.client.google.GcToolResults
import ftl.config.FtlConstants
import ftl.run.exception.FailureToken
import ftl.run.exception.FlankGeneralError
import ftl.run.exception.PermissionDenied
import ftl.run.exception.ProjectNotFound
import ftl.test.util.FlankTestRunner
import ftl.test.util.TestHelper.getThrowable
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
    fun `getDefaultBucket on 403 error should throw exception with specific message - no source`() {
        val expected = """
            Flank encountered a 403 error when running on project $projectId. Please verify this credential is authorized for the project and has the required permissions.
            Consider authentication with a Service Account https://flank.github.io/flank/#authenticate-with-a-service-account
            or with a Google account https://flank.github.io/flank/#authenticate-with-a-google-account
            
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
            every { GcToolResults.service.Projects().initializeSettings(projectId) } throws PermissionDenied(
                mockJSonException
            )
            val exception = getThrowable { GcToolResults.getDefaultBucket(projectId) }
            assertEquals(expected, exception.message)
        }
    }

    @Test
    fun `getDefaultBucket on 403 error should throw exception with specific message - with source`() {
        val expected = """
            Flank encountered a 403 error when running on project $projectId (from /Any/path/to/json.json). Please verify this credential is authorized for the project and has the required permissions.
            Consider authentication with a Service Account https://flank.github.io/flank/#authenticate-with-a-service-account
            or with a Google account https://flank.github.io/flank/#authenticate-with-a-google-account
            
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
            every { GcToolResults.service.Projects().initializeSettings(projectId) } throws PermissionDenied(
                mockJSonException
            )
            val exception = getThrowable { GcToolResults.getDefaultBucket(projectId, "/Any/path/to/json.json") }
            assertEquals(expected, exception.message)
        }
    }

    @Test(expected = FlankGeneralError::class)
    fun `getDefaultBucket on PermissionDenied error should throw FlankGeneralError`() {
        mockkObject(GcToolResults) {
            every { GcToolResults.service.applicationName } returns projectId
            every {
                GcToolResults.service.Projects().initializeSettings(projectId)
            } throws PermissionDenied(IOException())
            GcToolResults.getDefaultBucket(projectId)
        }
    }

    @Test(expected = FlankGeneralError::class)
    fun `getDefaultBucket on ProjectNotFound error should throw FlankGeneralError`() {
        mockkObject(GcToolResults) {
            every { GcToolResults.service.applicationName } returns projectId
            every {
                GcToolResults.service.Projects().initializeSettings(projectId)
            } throws ProjectNotFound(IOException())
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
            every { GcToolResults.service.Projects().initializeSettings(projectId) } throws ProjectNotFound(
                mockJSonException
            )
            val exception = getThrowable { GcToolResults.getDefaultBucket(projectId) }
            assertEquals(expected, exception.message)
        }
    }

    @Test
    fun `getDefaultBucket on 400 with authentication error should throw exception with specific message`() {
        val expected = """
Could not load user authentication, please
 - login again using command: flank auth login
 - or try again to use The Application Default Credentials variable to login
        """.trimIndent()
        mockkObject(GcToolResults) {
            every { GcToolResults.service.applicationName } returns FtlConstants.applicationName

            val exceptionBuilder = mockk<HttpResponseException.Builder>()
            every { exceptionBuilder.message } returns """
Caused by: com.google.api.client.http.HttpResponseException: 400 Bad Request
POST https://oauth2.googleapis.com/token
{
  "error": "invalid_grant",
  "error_description": "Bad Request"
}
            """.trimIndent()
            val mockJSonException = GoogleJsonResponseException(exceptionBuilder, null)
            every { GcToolResults.service.Projects().initializeSettings(projectId) } throws FailureToken(
                mockJSonException
            )
            val exception = getThrowable { GcToolResults.getDefaultBucket(projectId) }
            assertThat(exception).isInstanceOf(FlankGeneralError::class.java)
            assertEquals(expected, exception.message)
        }
    }

    @Test
    fun `should properly get Performance Metrics`() {
        // given
        val toolsStepResults = ToolResultsStep().setStepId("1").setExecutionId("2").setHistoryId("3").setProjectId("4")

        // when
        val response = GcToolResults.getPerformanceMetric(toolsStepResults)

        // then
        assertThat(response.stepId).isEqualTo(toolsStepResults.stepId)
        assertThat(response.executionId).isEqualTo(toolsStepResults.executionId)
        assertThat(response.projectId).isEqualTo(toolsStepResults.projectId)
        assertThat(response.historyId).isEqualTo(toolsStepResults.historyId)
    }
}
