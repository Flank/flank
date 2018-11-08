package ftl.reports.util

import ftl.args.IArgs
import ftl.args.IosArgs
import ftl.json.MatrixMap
import ftl.reports.CostReport
import ftl.reports.HtmlErrorReport
import ftl.reports.JUnitReport
import ftl.reports.MatrixResultsReport
import ftl.reports.xml.model.JUnitTestResult
import ftl.reports.xml.parseAndroidXml
import ftl.reports.xml.parseIosXml
import ftl.util.ArtifactRegex
import ftl.util.resolveLocalRunPath
import java.io.File
import java.nio.file.Paths

object ReportManager {

    private fun findXmlFiles(matrices: MatrixMap): List<File> {
        val xmlFiles = mutableListOf<File>()
        val rootFolder = File(resolveLocalRunPath(matrices))

        rootFolder.walk().forEach {
            if (it.name.matches(ArtifactRegex.testResultRgx)) {
                xmlFiles.add(it)
            }
        }

        return xmlFiles
    }

    private fun getWebLink(matrices: MatrixMap, xmlFile: File): String {
        val matrixFolder = xmlFile.parentFile.parentFile.name
        val matrixPath = Paths.get(matrices.runPath).fileName.resolve(matrixFolder).toString()
        var webLink = ""
        val savedMatrix = matrices.map.values.firstOrNull { it.gcsPath.endsWith(matrixPath) }
        if (savedMatrix != null) {
            webLink = savedMatrix.webLink
        } else {
            println("WARNING: Matrix path not found in JSON. $matrixPath")
        }
        return webLink
    }

    private fun processXml(matrices: MatrixMap, process: (file: File) -> JUnitTestResult): JUnitTestResult? {
        var mergedXml: JUnitTestResult? = null

        findXmlFiles(matrices).forEach { xmlFile ->
            val parsedXml = process(xmlFile)
            val webLink = getWebLink(matrices, xmlFile)

            parsedXml.testsuites?.forEach { testSuite ->
                testSuite.testcases?.forEach { testCase ->
                    testCase.webLink = webLink
                }
            }

            mergedXml = parsedXml.merge(mergedXml)
        }

        return mergedXml
    }

    /** Returns true if there were no test failures */
    fun generate(matrices: MatrixMap, args: IArgs): Int {
        val iosXml = args is IosArgs
        val testSuite = if (iosXml) {
            processXml(matrices, ::parseIosXml)
        } else {
            processXml(matrices, ::parseAndroidXml)
        }
        val testSuccessful = matrices.allSuccessful()

        listOf(
            CostReport,
            MatrixResultsReport
        ).map {
            it.run(matrices, testSuite, printToStdout = true)
        }

        JUnitReport.run(matrices, testSuite)

        if (!testSuccessful) {
            listOf(
                HtmlErrorReport
            ).map { it.run(matrices, testSuite) }
        }

        return matrices.exitCode()
    }
}
