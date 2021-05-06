package ftl.reports.outcome

import com.google.api.services.toolresults.model.Step
import com.google.testing.model.AndroidModel
import ftl.client.google.BillableMinutes
import ftl.client.google.DeviceType
import ftl.client.google.calculateAndroidBillableMinutes
import ftl.gc.GcTesting
import ftl.http.executeWithRetry
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemOutRule

class BillableMinutesTest {

    @get:Rule
    val output: SystemOutRule = SystemOutRule().enableLog().muteForSuccessfulTests()

    private val androidModels = listOf<AndroidModel>(
        make { id = "NexusLowRes"; form = DeviceType.VIRTUAL.name },
        make { id = "Nexus5"; form = DeviceType.VIRTUAL.name },
        make { id = "g3"; form = DeviceType.PHYSICAL.name },
        make { id = "sailfish"; form = DeviceType.PHYSICAL.name }
    )

    @Before
    fun setUp() {
        output.clearLog()
        mockkObject(GcTesting)
        every {
            GcTesting
                .get
                .testEnvironmentCatalog()
                .get(any())
                .setProjectId(any())
                .executeWithRetry()
        } returns make {
            androidDeviceCatalog = make { models = androidModels }
        }
    }

    @After
    fun tearDown() = unmockkAll()

    @Test
    fun `should return billing for virtual device only`() {
        val expected = 1L
        val step: Step = makeStep()

        val minutes = listOf(step).calculateAndroidBillableMinutes(projectId = "anyId", timeoutValue = 1000L)

        verifyBilling(minutes, expectedVirtual = expected)
    }

    @Test
    fun `should return billing for physical devices only`() {
        val expected = 1L
        val step: Step = makeStep(model = "g3")

        val minutes = listOf(step).calculateAndroidBillableMinutes(projectId = "anyId", timeoutValue = 1000L)

        verifyBilling(minutes, expectedPhysical = expected)
    }

    @Test
    fun `should return mixed billing`() {
        val expectedVirtual = 3L
        val expectedPhysical = 5L
        val step1: Step = makeStep(model = "nexuslowres", duration = 123)
        val step2: Step = makeStep(model = "SaIlFisH", duration = 245)

        val minutes = listOf(step1, step2).calculateAndroidBillableMinutes(projectId = "anyId", timeoutValue = 1000L)

        verifyBilling(minutes, expectedVirtual, expectedPhysical)
    }

    @Test
    fun `should return multiple mixed billing`() {
        val expectedVirtual = 9L
        val expectedPhysical = 16L
        val step1: Step = makeStep(model = "Nexus5", duration = 123)
        val step2: Step = makeStep(model = "SaIlFisH", duration = 245)
        val step3: Step = makeStep(model = "NEXUSLOWRES", duration = 352)
        val step4: Step = makeStep(model = "G3", duration = 657)

        val minutes = listOf(step1, step2, step3, step4).calculateAndroidBillableMinutes(projectId = "anyId", timeoutValue = 1000L)

        verifyBilling(minutes, expectedVirtual, expectedPhysical)
    }

    @Test
    fun `should return multiple mixed billing - timeouted`() {
        val timeout = 120L
        val expectedVirtual = 3L
        val expectedPhysical = 4L
        val step1: Step = makeStep(model = "Nexus5", duration = 23)
        val step2: Step = makeStep(model = "SaIlFisH", duration = 245)
        val step3: Step = makeStep(model = "NEXUSLOWRES", duration = 352)
        val step4: Step = makeStep(model = "G3", duration = 657)

        val minutes = listOf(step1, step2, step3, step4).calculateAndroidBillableMinutes(projectId = "anyId", timeoutValue = timeout)

        verifyBilling(minutes, expectedVirtual, expectedPhysical)
    }

    @Test
    fun `should return billing for physical device when unable to find device type`() {
        val expectedPhysical = 3L
        val modelId = "uncommon"
        val step: Step = makeStep(model = modelId, duration = 123)

        val minutes = listOf(step).calculateAndroidBillableMinutes(projectId = "anyId", timeoutValue = 1000L)

        verifyBilling(minutes, expectedPhysical = expectedPhysical)
        assertTrue(output.log.contains("Unable to find device type for $modelId. PHYSICAL used as fallback in cost calculations"))
    }
}

private fun makeStep(model: String = "NexusLowRes", duration: Long = 15L): Step = make {
    dimensionValue = listOf(make { key = "Model"; value = model })
    testExecutionStep = make {
        testTiming = make {
            testProcessDuration = make { seconds = duration }
        }
    }
}

private fun verifyBilling(billing: BillableMinutes, expectedVirtual: Long = 0, expectedPhysical: Long = 0) {
    assertEquals("Physical devices minutes should be $expectedPhysical", expectedPhysical, billing.physical)
    assertEquals("Virtual devices minutes should be $expectedVirtual", expectedVirtual, billing.virtual)
}
