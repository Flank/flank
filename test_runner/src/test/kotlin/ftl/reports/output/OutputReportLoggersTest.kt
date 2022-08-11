package ftl.reports.output

import com.google.common.truth.Truth.assertThat
import ftl.api.TestMatrix
import ftl.api.TestMatrixTest.Companion.historyId
import ftl.args.AndroidArgs
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class OutputReportLoggersTest {

    @Before
    fun setup() {
        outputReport.configure(outputReport.configuration.copy(enabled = true))
    }

    @Test
    fun `should log args`() {
        // given
        val args = AndroidArgs.default()

        // when
        outputReport.log(args)

        // then
        assertThat(outputReport.outputData).containsEntry("args", args)
    }

    @Test
    fun `should log weblinks`() {
        // given
        val args = AndroidArgs.default()

        // when
        outputReport.log(args)

        // then
        assertThat(outputReport.outputData).containsEntry("args", args)
    }

    @Test
    fun `should log test_results`() {
        // given
        val matrices = listOf(
            TestMatrix.Data(
                matrixId = "1",
                axes = listOf(TestMatrix.Outcome("device1")),
                appFileName = "any.apk",
                testFileName = "test-debug.apk",
                historyId = historyId
            )
        )
        val testResults = matrices.map {
            it.matrixId to mapOf(
                "app" to it.appFileName,
                "test-file" to it.testFileName,
                "test-axises" to it.axes
            )
        }.toMap()

        // when
        outputReport.log(matrices)

        // then
        assertThat(outputReport.outputData).containsEntry("test_results", testResults)
    }

    @Test
    fun `should log costs`() {
        // given
        val physical: BigDecimal = BigDecimal.ONE
        val virtual: BigDecimal = BigDecimal.TEN
        val total: BigDecimal = BigDecimal.ZERO

        // when
        outputReport.logCosts(physical, virtual, total)

        // then
        assertThat(outputReport.outputData).containsKey("cost")
    }

    @Test
    fun `should log billable minutes`() {
        // given
        val physical: BigDecimal = BigDecimal.ONE
        val virtual: BigDecimal = BigDecimal.TEN
        val total: BigDecimal = BigDecimal.ZERO

        // when
        outputReport.logBillableMinutes(physical, virtual, total)

        // then
        assertThat(outputReport.outputData).containsKey("billable-minutes")
    }
}
