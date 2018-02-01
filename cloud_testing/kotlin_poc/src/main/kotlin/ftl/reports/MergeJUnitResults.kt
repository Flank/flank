package ftl.reports

import com.sun.org.apache.xerces.internal.dom.DeferredElementImpl
import ftl.Main
import ftl.config.FtlConstants
import ftl.json.MatrixMap
import ftl.util.ArtifactRegex.testResultRgx
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import javax.xml.parsers.DocumentBuilderFactory

/** Used to create summary.csv from merging all the JUnit XML results **/
object MergeJUnitResults {

    fun run(matrixMap: MatrixMap) {
        val xmlFiles = mutableListOf<File>()
        val results = mutableMapOf<String, Int>()

        val rootFolderPath = Paths.get(FtlConstants.localResultsDir, matrixMap.runPath)
        val rootFolder = rootFolderPath.toFile()

        rootFolder.walk().forEach {
            if (it.name.matches(testResultRgx)) {
                xmlFiles.add(it)
            }
        }

        xmlFiles.forEach { file ->
            val xml = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)
            xml.normalizeDocument()
            xml.documentElement.normalize()

            val nodes = xml.getElementsByTagName("failure")

            repeat(nodes.length) { index ->
                val node: DeferredElementImpl = nodes.item(index) as DeferredElementImpl

                val parent = node.parentNode.attributes
                val className = parent.getNamedItem("classname").nodeValue
                val testName = parent.getNamedItem("name").nodeValue
                val key = "$className#$testName"
                results[key] = results.getOrDefault(key, 0) + 1
            }
        }

        val csv = Paths.get(rootFolderPath.toString(), "summary.csv")
        val csvData = StringBuilder()

        val sorted = results.toList().sortedByDescending { (_, value) -> value }.toMap()

        sorted.forEach {
            csvData.append(it.key, ',', it.value, "\n")
        }

        Files.write(csv, csvData.toString().toByteArray())
    }

    @JvmStatic
    fun main(args: Array<String>) {
        run(Main.lastMatrices())
    }
}
