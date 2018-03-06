package ftl.reports.util

import com.sun.org.apache.xerces.internal.dom.DeferredElementImpl
import ftl.json.MatrixMap
import ftl.reports.*
import ftl.run.TestRunner
import ftl.util.ArtifactRegex
import ftl.util.resolveLocalRunPath
import org.w3c.dom.NodeList
import java.io.File
import java.nio.file.Paths
import javax.xml.parsers.DocumentBuilderFactory

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

    private fun parseJUnitXml(matrices: MatrixMap): TestSuite {
        val testCases = mutableMapOf<String, TestResults>()
        val matrixMapValues = matrices.map.values

        val xmlFiles = findXmlFiles(matrices)
        var failureCount = 0
        var successCount = 0

        xmlFiles.forEach { file ->
            val xml = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)
            xml.normalizeDocument()
            xml.documentElement.normalize()

            val nodes: NodeList = xml.getElementsByTagName("testcase")

            val matrixFolder = file.parentFile.parentFile.name
            val matrixPath = Paths.get(matrices.runPath).fileName.resolve(matrixFolder).toString()

            repeat(nodes.length) { index ->
                val node: DeferredElementImpl = nodes.item(index) as DeferredElementImpl

                val attr = node.attributes
                val className = attr.getNamedItem("classname").nodeValue
                val testName = attr.getNamedItem("name").nodeValue
                val key = "$className#$testName"

                if (testCases[key] == null) {
                    testCases[key] = TestResults()
                }
                val testResults = testCases[key]!!

                var webLink = ""
                val savedMatrix = matrixMapValues.firstOrNull { it.gcsPath.endsWith(matrixPath) }
                if (savedMatrix != null) {
                    webLink = savedMatrix.webLink
                } else {
                    println("WARNING: Matrix path not found in JSON. $matrixPath")
                }

                val failureNode = node.getElementsByTagName("failure").item(0)

                if (failureNode == null) {
                    successCount += 1
                    testResults.successes.add(TestSuccess(webLink = webLink))
                } else {
                    failureCount += 1
                    val stackTrace = failureNode.firstChild.nodeValue
                    testResults.failures.add(TestFailure(
                            stackTrace = stackTrace,
                            webLink = webLink
                    ))
                }
            }
        }

        val sortedTestCases = testCases.toList()
                .sortedByDescending { (_, value) -> value.failures.size }.toMap()

        return TestSuite(
                totalTests = failureCount + successCount,
                failures = failureCount,
                successes = successCount,
                testCases = sortedTestCases
        )
    }

    fun generate(matrices: MatrixMap) {
        val testSuite = parseJUnitXml(matrices)

        // Print to stdout
        listOf(
                CostReport,
                MatrixReport
        ).map {
            it.run(matrices, testSuite, print = true)
        }

        // Generate report only
        listOf(
                HtmlReport,
                MatrixErrorReport,
                SummaryReport
        ).map { it.run(matrices, testSuite) }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val tmpPath = "../../../android-uno/teacher/results/2018-02-26_19:50:03.170000_jEIK"
        val matrix = TestRunner.matrixPathToObj(tmpPath)

        generate(matrix)
//        parseJUnitXml(matrix)
    }
}
