package ftl.reports

import ftl.args.IArgs
import ftl.config.FtlConstants.indent
import ftl.gc.GcStorage
import ftl.json.MatrixMap
import ftl.reports.util.IReport
import ftl.reports.xml.model.JUnitTestResult
import ftl.util.estimateCosts
import ftl.util.println
import java.io.StringWriter

/** Calculates cost based on the matrix map. Always run. */
object CostReport : IReport {

    override val extension = ".txt"

    private fun estimate(matrices: MatrixMap): String {
        var totalBillableVirtualMinutes = 0L
        var totalBillablePhysicalMinutes = 0L

        matrices.map.values.forEach {
            totalBillableVirtualMinutes += it.billableVirtualMinutes
            totalBillablePhysicalMinutes += it.billablePhysicalSecondsMinutes
        }

        return estimateCosts(totalBillableVirtualMinutes, totalBillablePhysicalMinutes)
    }

    private fun generate(matrices: MatrixMap): String {
        val cost = estimate(matrices)

        StringWriter().use { writer ->
            writer.println(reportName())
            cost.split("\n").forEach { writer.println(indent + it) }
            return writer.toString() + "\n"
        }
    }

    override fun run(matrices: MatrixMap, result: JUnitTestResult?, printToStdout: Boolean, args: IArgs) {
        val output = generate(matrices)
        if (printToStdout) print(output)
        write(matrices, output, args)
        GcStorage.uploadReportResult(output, args, fileName())
    }
}
