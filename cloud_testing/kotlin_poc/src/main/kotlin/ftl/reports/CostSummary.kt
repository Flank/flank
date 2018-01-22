package ftl.reports

import ftl.Main
import ftl.config.FtlConstants
import ftl.util.Billing
import java.nio.file.Files
import java.nio.file.Paths

object CostSummary {

    fun execute() {
        val matrices = Main.lastMatrices()
        var totalBillableMinutes = 0L

        matrices.map.values.forEach {
            totalBillableMinutes += it.billableMinutes
        }

        val cost = Billing.estimateCosts(totalBillableMinutes)

        val rootFolder = Paths.get(FtlConstants.localResultsDir, Main.lastGcsPath()).toString()
        val costSummaryPath = Paths.get(rootFolder, "cost_summary.txt")

        Files.write(costSummaryPath, cost.toByteArray())
    }

    @JvmStatic
    fun main(args: Array<String>) {
        execute()
    }
}
