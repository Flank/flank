package ftl.reports.output

import com.google.common.truth.Truth.assertThat
import ftl.args.AndroidArgs
import ftl.json.SavedMatrix
import ftl.reports.outcome.TestOutcome
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
            SavedMatrix(matrixId = "1", testAxises = listOf(TestOutcome("device1")))
        )
        val testResults = matrices.map {
            it.matrixId to it.testAxises
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
        outputReport.log(physical, virtual, total)

        // then
        assertThat(outputReport.outputData).containsKey("cost")
    }
}
