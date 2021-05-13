package ftl.reports.outcome

import com.google.api.services.toolresults.model.Environment
import com.google.api.services.toolresults.model.Step
import ftl.client.google.TestOutcomeContext
import ftl.client.google.calculateAndroidBillableMinutes
import ftl.client.google.createMatrixOutcomeSummary
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.reflect.full.createInstance

class CreateMatrixOutcomeSummaryKtTest {

    @Before
    fun setUp() {
        mockkStatic("ftl.reports.outcome.UtilKt")
        mockkStatic("ftl.client.google.BillableMinutesKt")
    }

    @After
    fun tearDown() = unmockkAll()

    @Test
    fun `should create TestOutcome list for success robo test - from environments`() {
        val env: Environment = make {
            environmentResult = make {
                outcome = make { summary = "success" }
            }
        }
        every { env.axisValue() } returns "anyDevice"
        val context = TestOutcomeContext(
            matrixId = "anyMatrix",
            projectId = "anyProject",
            testTimeout = Long.MAX_VALUE,
            steps = emptyList(),
            isRoboTest = true,
            environments = listOf(env)
        )

        val (_, result) = context.createMatrixOutcomeSummary()

        assertEquals("---", result[0].details)
        assertEquals("anyDevice", result[0].device)
        assertEquals("success", result[0].outcome)
    }

    @Test
    fun `should create TestOutcome list for failed robo test - from environments`() {
        val env: Environment = make {
            environmentResult = make {
                outcome = make {
                    summary = "failure"
                    failureDetail = make { failedRoboscript = true }
                }
            }
        }
        every { env.axisValue() } returns "anyDevice"
        val context = TestOutcomeContext(
            matrixId = "anyMatrix",
            projectId = "anyProject",
            testTimeout = Long.MAX_VALUE,
            steps = emptyList(),
            isRoboTest = true,
            environments = listOf(env)
        )

        val (_, result) = context.createMatrixOutcomeSummary()

        assertEquals("Test failed to run", result[0].details)
        assertEquals("anyDevice", result[0].device)
        assertEquals("failure", result[0].outcome)
    }

    @Test
    fun `should create TestOutcome list for success robo test - from steps`() {
        val steps: List<Step> = listOf(
            make {
                outcome = make { summary = "success" }
            }
        )
        every { steps[0].axisValue() } returns "anyDevice"
        every { steps.calculateAndroidBillableMinutes(any(), any()) } returns make()
        val context = TestOutcomeContext(
            matrixId = "anyMatrix",
            projectId = "anyProject",
            testTimeout = Long.MAX_VALUE,
            steps = steps,
            isRoboTest = true,
            environments = emptyList()
        )

        val (_, result) = context.createMatrixOutcomeSummary()

        assertEquals("---", result[0].details)
        assertEquals("anyDevice", result[0].device)
        assertEquals("success", result[0].outcome)
    }

    @Test
    fun `should create TestOutcome list for failed robo test - from steps`() {
        val steps: List<Step> = listOf(
            make {
                outcome = make {
                    summary = "failure"
                    failureDetail = make { failedRoboscript = true }
                }
            }
        )
        every { steps[0].axisValue() } returns "anyDevice"
        every { steps.calculateAndroidBillableMinutes(any(), any()) } returns make()
        val context = TestOutcomeContext(
            matrixId = "anyMatrix",
            projectId = "anyProject",
            testTimeout = Long.MAX_VALUE,
            steps = steps,
            isRoboTest = true,
            environments = emptyList()
        )

        val (_, result) = context.createMatrixOutcomeSummary()

        assertEquals("Test failed to run", result[0].details)
        assertEquals("anyDevice", result[0].device)
        assertEquals("failure", result[0].outcome)
    }
}

internal inline fun <reified T : Any> make(block: T.() -> Unit = {}): T = T::class.createInstance().apply(block)
