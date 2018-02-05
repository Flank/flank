package ftl.reports

import ftl.config.FtlConstants
import ftl.config.FtlConstants.indent
import ftl.json.MatrixMap
import ftl.run.TestRunner
import ftl.util.Billing
import java.nio.file.Files
import java.nio.file.Paths

object CostSummary {

    private fun estimate(matrices: MatrixMap): String {
        var totalBillableMinutes = 0L

        matrices.map.values.forEach {
            totalBillableMinutes += it.billableMinutes
        }

        return Billing.estimateCosts(totalBillableMinutes)
    }

    private fun write(gcsPath: String, cost: String) {
        val rootFolder = Paths.get(FtlConstants.localResultsDir, gcsPath).toString()
        val costSummaryPath = Paths.get(rootFolder, "cost_summary.txt")

        print("CostSummary")
        cost.split("\n").forEach { println(indent + it) }
        println()

        Files.write(costSummaryPath, cost.toByteArray())
    }

    fun run(matrices: MatrixMap) {
        val cost = estimate(matrices)
        write(matrices.runPath, cost)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        run(TestRunner.lastMatrices())
    }
}
