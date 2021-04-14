package ftl.reports.output

import com.google.common.truth.Truth.assertThat
import ftl.adapter.google.GcStorage
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.nio.file.Paths

class OutputReportTest {

    @get:Rule
    val root = TemporaryFolder()

    @Test
    fun `should update configuration`() {
        // given
        val initialConfig = outputReport.configuration
        val configuration = OutputReportConfiguration(
            enabled = true,
            local = OutputReportLocalConfiguration("x"),
            remote = OutputReportRemoteConfiguration(uploadReport = false)
        )

        // when
        outputReport.configure(configuration)

        // then
        assertThat(outputReport.configuration).isEqualTo(configuration)
        assertThat(initialConfig).isNotEqualTo(configuration)
    }

    @Test
    fun `should add node to report`() {
        // given
        outputReport.configure(outputReport.configuration.copy(enabled = true))

        // when
        outputReport.add("test", "node")

        // then
        assertThat(outputReport.outputData).containsEntry("test", "node")
    }

    @Test
    fun `should not generate report if disabled`() {
        // given
        val tempDirectory = root.newFolder("tmp")
        val configuration = OutputReportConfiguration(
            enabled = false,
            local = OutputReportLocalConfiguration(tempDirectory.toString()),
            remote = OutputReportRemoteConfiguration(uploadReport = false)
        )
        val expectedPath = Paths.get(configuration.local.storageDirectory, configuration.local.outputFile)

        // when
        outputReport.configure(configuration)
        outputReport.add("test", "node")
        outputReport.generate()

        // then
        assertThat(expectedPath.toFile().exists()).isFalse()
    }

    @Test
    fun `should not generate report if report type is NONE`() {
        // given
        val tempDirectory = root.newFolder("tmp")
        val configuration = OutputReportConfiguration(
            enabled = true,
            type = OutputReportType.NONE,
            local = OutputReportLocalConfiguration(tempDirectory.toString()),
            remote = OutputReportRemoteConfiguration(uploadReport = false)
        )
        val expectedPath = Paths.get(configuration.local.storageDirectory, configuration.local.outputFile)

        // when
        outputReport.configure(configuration)
        outputReport.add("test", "node")
        outputReport.generate()

        // then
        assertThat(expectedPath.toFile().exists()).isFalse()
    }

    @Test
    fun `should generate report if enabled`() {
        // given
        val tempDirectory = root.newFolder("tmp")
        val configuration = OutputReportConfiguration(
            enabled = true,
            type = OutputReportType.JSON,
            local = OutputReportLocalConfiguration(tempDirectory.toString()),
            remote = OutputReportRemoteConfiguration(uploadReport = false)
        )
        val expectedPath = Paths.get(configuration.local.storageDirectory, configuration.local.outputFile)

        // when
        outputReport.configure(configuration)
        outputReport.add("test", "node")
        outputReport.generate()

        // then
        assertThat(expectedPath.toFile().exists()).isTrue()

        // clean
        expectedPath.toFile().deleteRecursively()
    }

    @Test
    fun `should generate report if enabled and not upload to gcloud if disabled`() {
        // given
        val tempDirectory = root.newFolder("temp")
        val configuration = OutputReportConfiguration(
            enabled = true,
            type = OutputReportType.JSON,
            local = OutputReportLocalConfiguration(tempDirectory.toString()),
            remote = OutputReportRemoteConfiguration(uploadReport = false)
        )
        val expectedPath = Paths.get(configuration.local.storageDirectory, configuration.local.outputFile)

        // when
        outputReport.configure(configuration)
        outputReport.add("test", "node")
        mockkObject(GcStorage) {
            outputReport.generate()

            // then
            assertThat(expectedPath.toFile().exists()).isTrue()
            verify(exactly = 0) {
                GcStorage.upload(
                    expectedPath.toFile().absolutePath,
                    configuration.remote.resultsBucket,
                    configuration.remote.resultsDir
                )
            }
        }
    }
}
