package ftl.reports.api

import ftl.args.IArgs
import ftl.gc.GcTestMatrix
import ftl.json.MatrixMap
import ftl.reports.xml.model.JUnitTestResult

fun processXmlFromApi(
    matrices: MatrixMap,
    args: IArgs
): JUnitTestResult = GcTestMatrix.refresh(
    // Android uses server side sharding, it results only one matrix, so we can simply get first.
    matrices.map.values.first().matrixId,
    args
).createJUnitTestResult()
