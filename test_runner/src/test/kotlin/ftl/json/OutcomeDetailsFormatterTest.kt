package ftl.json

import com.google.api.services.toolresults.model.FailureDetail
import com.google.api.services.toolresults.model.Outcome
import ftl.reports.api.data.TestSuiteOverviewData
import ftl.util.StepOutcome
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test

internal class OutcomeDetailsFormatterTest {

    @After
    fun tearDown() = unmockkAll()

    @Test
    fun `should return correct outcome details for success`() {
        // given
        val mockedOutcome = mockk<Outcome> {
            every { summary } returns StepOutcome.success
            every { successDetail } returns mockk { every { otherNativeCrash } returns false }
        }
        val testSuiteOverviewData = TestSuiteOverviewData(12, 0, 0, 3, 2, 0.0)
        val successCount = with(testSuiteOverviewData) { total - errors - failures - flakes - skipped }
        val expectedMessage = "$successCount test cases passed, " +
            "${testSuiteOverviewData.skipped} skipped, " +
            "${testSuiteOverviewData.flakes} flaky"

        // when
        val result = mockedOutcome.getDetails(testSuiteOverviewData)

        // then
        assertEquals(expectedMessage, result)
    }

    @Test
    fun `should return correct outcome details for success with other native crash`() {
        // given
        val mockedOutcome = mockk<Outcome> {
            every { summary } returns StepOutcome.success
            every { successDetail } returns mockk { every { otherNativeCrash } returns true }
        }
        val testSuiteOverviewData = TestSuiteOverviewData(12, 0, 0, 3, 2, 0.0)
        val successCount = with(testSuiteOverviewData) { total - errors - failures - flakes - skipped }
        val expectedMessage = "$successCount test cases passed, " +
            "${testSuiteOverviewData.skipped} skipped, " +
            "${testSuiteOverviewData.flakes} flaky" +
            " (Native crash)"

        // when
        val result = mockedOutcome.getDetails(testSuiteOverviewData)

        // then
        assertEquals(expectedMessage, result)
    }

    @Test
    fun `should return correct outcome details for failed-other with test overview data`() {
        // given
        val mockedOutcome = mockk<Outcome> {
            every { summary } returns StepOutcome.failure
            every { failureDetail } returns mockk(relaxed = true) {}
        }
        val testSuiteOverviewData = TestSuiteOverviewData(12, 3, 3, 3, 2, 0.0)
        val expectedMessage = "${testSuiteOverviewData.failures} test cases failed, " +
            "${testSuiteOverviewData.errors} errors, " +
            "1 passed, " +
            "${testSuiteOverviewData.skipped} skipped, " +
            "${testSuiteOverviewData.flakes} flaky"

        // when
        val result = mockedOutcome.getDetails(testSuiteOverviewData)

        // then
        assertEquals(expectedMessage, result)
    }

    @Test
    fun `should return correct outcome details for failed-crashed`() {
        // given
        val mockedOutcome = mockk<Outcome> {
            every { summary } returns StepOutcome.failure
            every { failureDetail } returns mockk {
                every { crashed } returns true
                every { timedOut } returns false
                every { notInstalled } returns false
                every { otherNativeCrash } returns false
            }
        }
        val expectedMessage = "Application crashed"

        // when
        val result = mockedOutcome.getDetails(null)

        // then
        assertEquals(expectedMessage, result)
    }

    @Test
    fun `should return correct outcome details for failed-timedOut`() {
        // given
        val mockedOutcome = mockk<Outcome> {
            every { summary } returns StepOutcome.failure
            every { failureDetail } returns mockk {
                every { crashed } returns false
                every { timedOut } returns true
                every { notInstalled } returns false
                every { otherNativeCrash } returns false
            }
        }
        val expectedMessage = "Test timed out"

        // when
        val result = mockedOutcome.getDetails(null)

        // then
        assertEquals(expectedMessage, result)
    }

    @Test
    fun `should return correct outcome details for failed-notInstalled`() {
        // given
        val mockedOutcome = mockk<Outcome> {
            every { summary } returns StepOutcome.failure
            every { failureDetail } returns mockk {
                every { crashed } returns false
                every { timedOut } returns false
                every { notInstalled } returns true
                every { otherNativeCrash } returns false
            }
        }
        val expectedMessage = "App failed to install"

        // when
        val result = mockedOutcome.getDetails(null)

        // then
        assertEquals(expectedMessage, result)
    }

    @Test
    fun `Should return unknown failure message for failed-null testSuiteOverviewData`() {
        // given
        val mockedOutcome = mockk<Outcome> {
            every { summary } returns StepOutcome.failure
            every { failureDetail } returns mockk(relaxed = true) {}
        }
        val expectedMessage = "Unknown failure"

        // when
        val result = mockedOutcome.getDetails(null)

        // then
        assertEquals(expectedMessage, result)
    }

    @Test
    fun `Should return unknown failure message for failed-null testSuiteOverviewData and failure details present`() {
        // given
        val mockedOutcome = mockk<Outcome> {
            every { summary } returns StepOutcome.failure
            every { failureDetail } returns null
        }
        val expectedMessage = "Unknown failure"

        // when
        val result = mockedOutcome.getDetails(null)

        // then
        assertEquals(expectedMessage, result)
    }

    @Test
    fun `should contains message about native crash when it happens`() {
        // given
        val mockedOutcome = mockk<Outcome> {
            every { summary } returns StepOutcome.failure
            every { failureDetail } returns mockk {
                every { crashed } returns false
                every { timedOut } returns false
                every { notInstalled } returns true
                every { otherNativeCrash } returns true
            }
        }
        val expectedMessage = "App failed to install (Native crash)"

        // when
        val result = mockedOutcome.getDetails(null)

        // then
        assertEquals(expectedMessage, result)
    }

    @Test
    fun `should return correct outcome details for inconclusive-infrastructureFailure`() {
        // given
        val mockedOutcome = mockk<Outcome> {
            every { summary } returns StepOutcome.inconclusive
            every { inconclusiveDetail } returns mockk { every { infrastructureFailure } returns true }
        }
        val expectedMessage = "Infrastructure failure"

        // when
        val result = mockedOutcome.getDetails(null)

        // then
        assertEquals(expectedMessage, result)
    }

    @Test
    fun `should return correct outcome details for inconclusive-abortedByUser`() {
        // given
        val mockedOutcome = mockk<Outcome> {
            every { summary } returns StepOutcome.inconclusive
            every { inconclusiveDetail } returns mockk() {
                every { infrastructureFailure } returns false
                every { abortedByUser } returns true
            }
        }
        val expectedMessage = "Test run aborted by user"

        // when
        val result = mockedOutcome.getDetails(null)

        // then
        assertEquals(expectedMessage, result)
    }

    @Test
    fun `should return correct outcome details for inconclusive-other reason`() {
        // given
        val mockedOutcome = mockk<Outcome> {
            every { summary } returns StepOutcome.inconclusive
            every { inconclusiveDetail } returns mockk {
                every { infrastructureFailure } returns false
                every { abortedByUser } returns false
            }
        }
        val expectedMessage = "Unknown reason"

        // when
        val result = mockedOutcome.getDetails(null)

        // then
        assertEquals(expectedMessage, result)
    }

    @Test
    fun `should return correct outcome details for skipped-incompatibleDevice`() {
        // given
        val mockedOutcome = mockk<Outcome> {
            every { summary } returns StepOutcome.skipped
            every { skippedDetail } returns mockk {
                every { incompatibleDevice } returns true
                every { incompatibleArchitecture } returns false
                every { incompatibleAppVersion } returns false
            }
        }
        val expectedMessage = "Incompatible device/OS combination"

        // when
        val result = mockedOutcome.getDetails(null)

        // then
        assertEquals(expectedMessage, result)
    }

    @Test
    fun `should return correct outcome details for skipped-incompatibleArchitecture`() {
        // given
        val mockedOutcome = mockk<Outcome> {
            every { summary } returns StepOutcome.skipped
            every { skippedDetail } returns mockk {
                every { incompatibleDevice } returns false
                every { incompatibleArchitecture } returns true
                every { incompatibleAppVersion } returns false
            }
        }
        val expectedMessage = "App does not support the device architecture"

        // when
        val result = mockedOutcome.getDetails(null)

        // then
        assertEquals(expectedMessage, result)
    }

    @Test
    fun `should return correct outcome details for skipped-incompatibleAppVersion`() {
        // given
        val mockedOutcome = mockk<Outcome> {
            every { summary } returns StepOutcome.skipped
            every { skippedDetail } returns mockk {
                every { incompatibleDevice } returns false
                every { incompatibleArchitecture } returns false
                every { incompatibleAppVersion } returns true
            }
        }
        val expectedMessage = "App does not support the OS version"

        // when
        val result = mockedOutcome.getDetails(null)

        // then
        assertEquals(expectedMessage, result)
    }

    @Test
    fun `should return correct outcome details for skipped-other reason`() {
        // given
        val mockedOutcome = mockk<Outcome> {
            every { summary } returns StepOutcome.skipped
            every { skippedDetail } returns mockk {
                every { incompatibleDevice } returns false
                every { incompatibleArchitecture } returns false
                every { incompatibleAppVersion } returns false
            }
        }
        val expectedMessage = "Unknown reason"

        // when
        val result = mockedOutcome.getDetails(null)

        // then
        assertEquals(expectedMessage, result)
    }

    @Test
    fun `should return correct outcome details for unset reason`() {
        // given
        val mockedOutcome = mockk<Outcome> {
            every { summary } returns StepOutcome.unset
        }
        val expectedMessage = "Unset outcome"

        // when
        val result = mockedOutcome.getDetails(null)

        // then
        assertEquals(expectedMessage, result)
    }

    @Test // https://github.com/flank/flank/issues/1026
    fun `should not throw when otherNativeCrash is null`() {
        FailureDetail().apply {
            otherNativeCrash = null
        }.getFailureOutcomeDetails(null)
    }

    @Test
    fun `should print message for failed robo test`() {
        val mockedOutcome = mockk<Outcome> {
            every { summary } returns StepOutcome.failure
            every { failureDetail } returns FailureDetail().apply { failedRoboscript = true }
        }

        val result = mockedOutcome.getDetails(null, true)

        assertEquals("Test failed to run", result)
    }
}
