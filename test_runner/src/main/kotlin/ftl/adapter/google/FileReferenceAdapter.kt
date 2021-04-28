package ftl.adapter.google

import ftl.api.FileReference
import ftl.reports.xml.model.JUnitTestResult

internal fun String.toApiModel(fileReference: FileReference) = fileReference.copy(local = this)

internal fun JUnitTestResult?.toApiModel() = JUnitTestResult(testsuites = this?.testsuites)
