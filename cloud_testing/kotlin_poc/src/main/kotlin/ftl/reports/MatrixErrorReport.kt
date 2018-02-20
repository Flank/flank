package ftl.reports

import com.sun.org.apache.xerces.internal.dom.DeferredElementImpl
import ftl.config.FtlConstants
import ftl.json.MatrixMap
import ftl.run.TestRunner
import ftl.util.ArtifactRegex.testResultRgx
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import javax.xml.parsers.DocumentBuilderFactory

/** Used to create matrix error report **/
object MatrixErrorReport {

    data class TestFailure(
            val message: String,
            val webLink: String
    )

    fun run(matrixMap: MatrixMap) {
        val xmlFiles = mutableListOf<File>()
        val results = mutableMapOf<String, MutableList<TestFailure>>()

        val rootFolderPath = Paths.get(FtlConstants.localResultsDir, matrixMap.runPath)
        val rootFolder = rootFolderPath.toFile()

        rootFolder.walk().forEach {
            if (it.name.matches(testResultRgx)) {
                xmlFiles.add(it)
            }
        }

        val matrixMapValues = matrixMap.map.values

        xmlFiles.forEach { file ->
            val xml = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)
            xml.normalizeDocument()
            xml.documentElement.normalize()

            val nodes = xml.getElementsByTagName("failure")

            val matrixFolder = file.parentFile.parentFile.name
            val matrixPath = matrixMap.runPath + "/" + matrixFolder

            repeat(nodes.length) { index ->
                val node: DeferredElementImpl = nodes.item(index) as DeferredElementImpl

                val parent = node.parentNode.attributes
                val className = parent.getNamedItem("classname").nodeValue
                val testName = parent.getNamedItem("name").nodeValue
                val key = "$className#$testName"

                val failureMessage = node.firstChild.nodeValue

                val webLink = matrixMapValues.first { it.gcsPath.endsWith(matrixPath) }.webLink

                val testFailure = TestFailure(
                        message = failureMessage,
                        webLink = webLink
                )

                if (results[key] == null) {
                    results[key] = mutableListOf()
                }

                results[key]!!.add(testFailure)
            }
        }

        val csv = Paths.get(rootFolderPath.toString(), "${this.javaClass.simpleName}.csv")
        val csvData = StringBuilder()
        csvData.puts(listOf("Test Name", "Link", "Failure"))

        val sorted = results.toList()
                .sortedByDescending { (_, value) -> value.size }.toMap()

        sorted.forEach {
            // com.a.b.c.TestClass#testName => TestClass#testName
            val testName = it.key.split(".").last()
            csvData.puts(listOf(testName, it.value.size.toString() + "x"))
            it.value.forEach {
                csvData.puts(listOf("", it.webLink, it.message.split("\n").firstOrNull() ?: ""))
            }
        }

        Files.write(csv, csvData.toString().toByteArray())
    }

    private fun StringBuilder.puts(items: List<String>) {
        val str = items.joinToString("\t") + "\n"
        this.append(str)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        run(TestRunner.lastMatrices())
    }
}
