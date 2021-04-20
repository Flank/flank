package ftl.environment

import com.google.common.truth.Truth.assertThat
import ftl.api.fetchSoftwareCatalogAsTable
import ftl.test.util.FlankTestRunner
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(FlankTestRunner::class)
class ListProvidedSoftwareTest {

    @After
    fun tearDown() = unmockkAll()

    @Test
    fun `should print provided software as a table`() {
        // given
        val expectedOrchestratorVersion = "1.0.0"
        val expectedHeader = "ORCHESTRATOR VERSION"

        // when
        val output = fetchSoftwareCatalogAsTable()

        // then
        assertThat(output.lines()[0]).doesNotContain("│")
        assertThat(output.lines()[1]).contains(expectedHeader)
        assertThat(output.lines()[3]).contains(expectedOrchestratorVersion)
    }
}
