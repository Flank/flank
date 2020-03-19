package ftl.reports.api

import ftl.args.IArgs
import ftl.gc.GcTestMatrix
import ftl.json.MatrixMap
import ftl.reports.xml.model.JUnitTestResult
import kotlinx.coroutines.runBlocking

fun processXmlFromApi(
    matrices: MatrixMap,
    args: IArgs
): JUnitTestResult = runBlocking {
    GcTestMatrix.refresh(
        // Android uses server side sharding, it results only one matrix, so we can simply get first.
        matrices.map.values.first().matrixId,
        args.project
    )
}.createJUnitTestResult()
